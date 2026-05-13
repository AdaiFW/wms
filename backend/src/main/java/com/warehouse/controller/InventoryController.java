package com.warehouse.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.common.Result;
import com.warehouse.dto.InventoryUpdateDto;
import com.warehouse.entity.Inventory;
import com.warehouse.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public Result<Page<Map<String, Object>>> list(@RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size,
                                                   @RequestParam(required = false) String keyword) {
        return Result.ok(inventoryService.page(page, size, keyword));
    }

    @GetMapping("/goods/{goodsId}")
    public Result<Inventory> getByGoods(@PathVariable Long goodsId) {
        return Result.ok(inventoryService.getByGoodsId(goodsId));
    }

    @PutMapping("/config")
    public Result<Void> updateConfig(@Valid @RequestBody InventoryUpdateDto dto) {
        inventoryService.updateConfig(dto);
        return Result.ok();
    }

    @GetMapping("/alerts")
    public Result<List<Map<String, Object>>> alerts() {
        return Result.ok(inventoryService.getAlerts());
    }

    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        return Result.ok(inventoryService.getOverview());
    }
}
