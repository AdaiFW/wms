package com.warehouse.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.common.exception.BusinessException;
import com.warehouse.dto.StockOutDto;
import com.warehouse.entity.StockOut;
import com.warehouse.security.LoginUser;
import com.warehouse.mapper.StockOutMapper;
import com.warehouse.service.StockOutService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockOutServiceImpl implements StockOutService {

    private final StockOutMapper stockOutMapper;
    private final InventoryServiceImpl inventoryService;

    @Override
    public Page<StockOut> page(Integer pageNum, Integer pageSize, String keyword) {
        LambdaQueryWrapper<StockOut> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.like(StockOut::getStockOutNo, keyword);
        }
        wrapper.orderByDesc(StockOut::getCreateTime);
        return stockOutMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public StockOut getById(Long id) {
        return stockOutMapper.selectById(id);
    }

    @Override
    @Transactional
    public StockOut create(StockOutDto dto) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        StockOut stockOut = new StockOut();
        stockOut.setStockOutNo("OUT" + DateUtil.format(DateUtil.date(), "yyyyMMddHHmmss")
                + RandomUtil.randomNumbers(4));
        stockOut.setGoodsId(dto.getGoodsId());
        stockOut.setQuantity(dto.getQuantity());
        stockOut.setUnitPrice(dto.getUnitPrice());
        stockOut.setTotalPrice(dto.getTotalPrice());
        stockOut.setTargetInfo(dto.getTargetInfo());
        stockOut.setOperatorId(loginUser.getUserId());
        stockOut.setRemark(dto.getRemark());
        stockOut.setStatus(0);
        stockOutMapper.insert(stockOut);

        confirm(stockOut.getId());
        return stockOut;
    }

    @Override
    @Transactional
    public void confirm(Long id) {
        StockOut stockOut = stockOutMapper.selectById(id);
        if (stockOut == null) throw new BusinessException("出库单不存在");
        if (stockOut.getStatus() != 0) throw new BusinessException("出库单状态不允许确认");

        inventoryService.decreaseStock(stockOut.getGoodsId(), stockOut.getQuantity(),
                stockOut.getOperatorId(), stockOut.getStockOutNo());

        stockOut.setStatus(1);
        stockOutMapper.updateById(stockOut);
    }

    @Override
    public void cancel(Long id) {
        StockOut stockOut = stockOutMapper.selectById(id);
        if (stockOut == null) throw new BusinessException("出库单不存在");
        if (stockOut.getStatus() == 2) throw new BusinessException("出库单已取消");

        if (stockOut.getStatus() == 1) {
            inventoryService.increaseStock(stockOut.getGoodsId(), stockOut.getQuantity(),
                    stockOut.getOperatorId(), stockOut.getStockOutNo() + "-CANCEL");
        }
        stockOut.setStatus(2);
        stockOutMapper.updateById(stockOut);
    }
}
