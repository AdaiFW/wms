package com.warehouse.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.common.exception.BusinessException;
import com.warehouse.dto.InventoryUpdateDto;
import com.warehouse.entity.Goods;
import com.warehouse.entity.Inventory;
import com.warehouse.entity.InventoryLog;
import com.warehouse.mapper.GoodsMapper;
import com.warehouse.mapper.InventoryLogMapper;
import com.warehouse.mapper.InventoryMapper;
import com.warehouse.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryMapper inventoryMapper;
    private final InventoryLogMapper inventoryLogMapper;
    private final GoodsMapper goodsMapper;

    @Override
    public Page<Map<String, Object>> page(Integer pageNum, Integer pageSize, String keyword) {
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Inventory::getUpdateTime);
        Page<Inventory> invPage = inventoryMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        Page<Map<String, Object>> result = new Page<>(pageNum, pageSize);
        result.setTotal(invPage.getTotal());
        result.setRecords(inventoryMapper.selectInventoryWithGoods());
        return result;
    }

    @Override
    public Inventory getByGoodsId(Long goodsId) {
        return inventoryMapper.selectOne(new LambdaQueryWrapper<Inventory>()
                .eq(Inventory::getGoodsId, goodsId));
    }

    @Override
    @Transactional
    public void updateConfig(InventoryUpdateDto dto) {
        Inventory inv = inventoryMapper.selectOne(new LambdaQueryWrapper<Inventory>()
                .eq(Inventory::getGoodsId, dto.getGoodsId()));
        if (inv == null) {
            Goods goods = goodsMapper.selectById(dto.getGoodsId());
            if (goods == null) throw new BusinessException("商品不存在");
            inv = new Inventory();
            inv.setGoodsId(dto.getGoodsId());
            inv.setQuantity(0);
            inv.setMinStock(dto.getMinStock() != null ? dto.getMinStock() : 0);
            inv.setMaxStock(dto.getMaxStock() != null ? dto.getMaxStock() : 99999);
            inv.setAlertThreshold(dto.getAlertThreshold());
            inventoryMapper.insert(inv);
        } else {
            if (dto.getMinStock() != null) inv.setMinStock(dto.getMinStock());
            if (dto.getMaxStock() != null) inv.setMaxStock(dto.getMaxStock());
            inv.setAlertThreshold(dto.getAlertThreshold());
            inventoryMapper.updateById(inv);
        }
    }

    @Override
    public List<Map<String, Object>> getAlerts() {
        return inventoryMapper.selectAlertInventories(null);
    }

    @Override
    public Map<String, Object> getOverview() {
        Long totalGoods = goodsMapper.selectCount(null);
        List<Map<String, Object>> alerts = inventoryMapper.selectAlertInventories(null);
        Long totalStock = inventoryMapper.selectList(null).stream()
                .mapToLong(Inventory::getQuantity).sum();

        return Map.of(
                "totalGoods", totalGoods,
                "totalStock", totalStock,
                "alertCount", (long) alerts.size(),
                "alerts", alerts
        );
    }

    /**
     * 入库扣库存 — 使用乐观锁重试
     */
    @Transactional
    public boolean increaseStock(Long goodsId, int qty, Long operatorId, String bizNo) {
        int retry = 3;
        while (retry-- > 0) {
            Inventory inv = inventoryMapper.selectOne(new LambdaQueryWrapper<Inventory>()
                    .eq(Inventory::getGoodsId, goodsId));
            if (inv == null) {
                inv = new Inventory();
                inv.setGoodsId(goodsId);
                inv.setQuantity(0);
                inv.setMinStock(0);
                inv.setMaxStock(99999);
                inv.setAlertThreshold(10);
                inventoryMapper.insert(inv);
            }

            int beforeQty = inv.getQuantity();
            int afterQty = beforeQty + qty;

            LambdaUpdateWrapper<Inventory> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Inventory::getId, inv.getId())
                         .eq(Inventory::getVersion, inv.getVersion())
                         .set(Inventory::getQuantity, afterQty)
                         .set(Inventory::getVersion, inv.getVersion() + 1);

            int rows = inventoryMapper.update(null, updateWrapper);
            if (rows > 0) {
                InventoryLog log = new InventoryLog();
                log.setGoodsId(goodsId);
                log.setBeforeQuantity(beforeQty);
                log.setChangeQuantity(qty);
                log.setAfterQuantity(afterQty);
                log.setType("IN");
                log.setBizNo(bizNo);
                log.setOperatorId(operatorId);
                inventoryLogMapper.insert(log);
                return true;
            }
            try { Thread.sleep(50); } catch (InterruptedException ignored) {}
        }
        throw new BusinessException("库存更新失败，请重试");
    }

    /**
     * 出库扣库存 — 使用乐观锁重试
     */
    @Transactional
    public boolean decreaseStock(Long goodsId, int qty, Long operatorId, String bizNo) {
        int retry = 3;
        while (retry-- > 0) {
            Inventory inv = inventoryMapper.selectOne(new LambdaQueryWrapper<Inventory>()
                    .eq(Inventory::getGoodsId, goodsId));
            if (inv == null) {
                throw new BusinessException("库存记录不存在");
            }
            if (inv.getQuantity() < qty) {
                throw new BusinessException("库存不足，当前库存: " + inv.getQuantity());
            }

            int beforeQty = inv.getQuantity();
            int afterQty = beforeQty - qty;

            LambdaUpdateWrapper<Inventory> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Inventory::getId, inv.getId())
                         .eq(Inventory::getVersion, inv.getVersion())
                         .set(Inventory::getQuantity, afterQty)
                         .set(Inventory::getVersion, inv.getVersion() + 1);

            int rows = inventoryMapper.update(null, updateWrapper);
            if (rows > 0) {
                InventoryLog log = new InventoryLog();
                log.setGoodsId(goodsId);
                log.setBeforeQuantity(beforeQty);
                log.setChangeQuantity(-qty);
                log.setAfterQuantity(afterQty);
                log.setType("OUT");
                log.setBizNo(bizNo);
                log.setOperatorId(operatorId);
                inventoryLogMapper.insert(log);
                return true;
            }
            try { Thread.sleep(50); } catch (InterruptedException ignored) {}
        }
        throw new BusinessException("库存更新失败，请重试");
    }
}
