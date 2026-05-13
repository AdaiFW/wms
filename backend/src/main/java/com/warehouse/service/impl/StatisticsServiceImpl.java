package com.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.warehouse.entity.*;
import com.warehouse.mapper.*;
import com.warehouse.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final GoodsMapper goodsMapper;
    private final InventoryMapper inventoryMapper;
    private final StockInMapper stockInMapper;
    private final StockOutMapper stockOutMapper;
    private final SupplierMapper supplierMapper;

    @Override
    public Map<String, Object> getDashboard() {
        Long totalGoods = goodsMapper.selectCount(null);
        Long totalSuppliers = supplierMapper.selectCount(null);
        List<Inventory> inventories = inventoryMapper.selectList(null);
        Long totalStock = inventories.stream().mapToLong(Inventory::getQuantity).sum();
        Long alertCount = inventories.stream()
                .filter(i -> i.getQuantity() <= i.getAlertThreshold()).count();

        Long todayStockIn = stockInMapper.selectCount(new LambdaQueryWrapper<StockIn>()
                .apply("DATE(create_time) = CURDATE()"));
        Long todayStockOut = stockOutMapper.selectCount(new LambdaQueryWrapper<StockOut>()
                .apply("DATE(create_time) = CURDATE()"));

        return Map.of(
                "totalGoods", totalGoods,
                "totalSuppliers", totalSuppliers,
                "totalStock", totalStock,
                "alertCount", alertCount,
                "todayStockIn", todayStockIn,
                "todayStockOut", todayStockOut
        );
    }

    @Override
    public Map<String, Object> getInventoryStats() {
        List<Map<String, Object>> alerts = inventoryMapper.selectAlertInventories(null);
        return Map.of("total", alerts.size(), "items", alerts);
    }

    @Override
    public Map<String, Object> getStockInOutTrend(Integer days) {
        List<Map<String, Object>> trend = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            Long inCount = stockInMapper.selectCount(new LambdaQueryWrapper<StockIn>()
                    .apply("DATE(create_time) = {0}", date)
                    .eq(StockIn::getStatus, 1));
            Long outCount = stockOutMapper.selectCount(new LambdaQueryWrapper<StockOut>()
                    .apply("DATE(create_time) = {0}", date)
                    .eq(StockOut::getStatus, 1));
            trend.add(Map.of("date", date.toString(), "stockIn", inCount, "stockOut", outCount));
        }
        return Map.of("trend", trend);
    }
}
