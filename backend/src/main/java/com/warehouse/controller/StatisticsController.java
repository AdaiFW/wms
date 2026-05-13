package com.warehouse.controller;

import com.warehouse.common.Result;
import com.warehouse.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        return Result.ok(statisticsService.getDashboard());
    }

    @GetMapping("/inventory")
    public Result<Map<String, Object>> inventory() {
        return Result.ok(statisticsService.getInventoryStats());
    }

    @GetMapping("/trend")
    public Result<Map<String, Object>> trend(@RequestParam(defaultValue = "7") Integer days) {
        return Result.ok(statisticsService.getStockInOutTrend(days));
    }
}
