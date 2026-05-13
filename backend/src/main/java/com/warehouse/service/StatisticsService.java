package com.warehouse.service;

import java.util.Map;

public interface StatisticsService {
    Map<String, Object> getDashboard();
    Map<String, Object> getInventoryStats();
    Map<String, Object> getStockInOutTrend(Integer days);
}
