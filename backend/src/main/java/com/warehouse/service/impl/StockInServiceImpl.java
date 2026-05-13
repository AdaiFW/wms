package com.warehouse.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.common.exception.BusinessException;
import com.warehouse.dto.StockInDto;
import com.warehouse.entity.Inventory;
import com.warehouse.entity.StockIn;
import com.warehouse.mapper.InventoryMapper;
import com.warehouse.mapper.StockInMapper;
import com.warehouse.security.LoginUser;
import com.warehouse.service.StockInService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockInServiceImpl implements StockInService {

    private final StockInMapper stockInMapper;
    private final InventoryMapper inventoryMapper;
    private final InventoryServiceImpl inventoryService;

    @Override
    public Page<StockIn> page(Integer pageNum, Integer pageSize, String keyword) {
        LambdaQueryWrapper<StockIn> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.like(StockIn::getStockInNo, keyword);
        }
        wrapper.orderByDesc(StockIn::getCreateTime);
        return stockInMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public StockIn getById(Long id) {
        return stockInMapper.selectById(id);
    }

    @Override
    @Transactional
    public StockIn create(StockInDto dto) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        StockIn stockIn = new StockIn();
        stockIn.setStockInNo("IN" + DateUtil.format(DateUtil.date(), "yyyyMMddHHmmss")
                + RandomUtil.randomNumbers(4));
        stockIn.setGoodsId(dto.getGoodsId());
        stockIn.setQuantity(dto.getQuantity());
        stockIn.setUnitPrice(dto.getUnitPrice());
        stockIn.setTotalPrice(dto.getTotalPrice());
        stockIn.setSupplierId(dto.getSupplierId());
        stockIn.setOperatorId(loginUser.getUserId());
        stockIn.setRemark(dto.getRemark());
        stockIn.setStatus(0); // 草稿
        stockInMapper.insert(stockIn);

        // 直接确认入库
        confirm(stockIn.getId());
        return stockIn;
    }

    @Override
    @Transactional
    public void confirm(Long id) {
        StockIn stockIn = stockInMapper.selectById(id);
        if (stockIn == null) throw new BusinessException("入库单不存在");
        if (stockIn.getStatus() != 0) throw new BusinessException("入库单状态不允许确认");

        inventoryService.increaseStock(stockIn.getGoodsId(), stockIn.getQuantity(),
                stockIn.getOperatorId(), stockIn.getStockInNo());

        stockIn.setStatus(1);
        stockInMapper.updateById(stockIn);
    }

    @Override
    public void cancel(Long id) {
        StockIn stockIn = stockInMapper.selectById(id);
        if (stockIn == null) throw new BusinessException("入库单不存在");
        if (stockIn.getStatus() == 2) throw new BusinessException("入库单已取消");

        if (stockIn.getStatus() == 1) {
            inventoryService.decreaseStock(stockIn.getGoodsId(), stockIn.getQuantity(),
                    stockIn.getOperatorId(), stockIn.getStockInNo() + "-CANCEL");
        }
        stockIn.setStatus(2);
        stockInMapper.updateById(stockIn);
    }
}
