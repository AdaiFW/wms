package com.warehouse.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.common.Result;
import com.warehouse.dto.StockInDto;
import com.warehouse.entity.StockIn;
import com.warehouse.service.StockInService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stock-in")
@RequiredArgsConstructor
public class StockInController {

    private final StockInService stockInService;

    @GetMapping
    public Result<Page<StockIn>> list(@RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer size,
                                       @RequestParam(required = false) String keyword) {
        return Result.ok(stockInService.page(page, size, keyword));
    }

    @GetMapping("/{id}")
    public Result<StockIn> get(@PathVariable Long id) {
        return Result.ok(stockInService.getById(id));
    }

    @PostMapping
    public Result<StockIn> create(@Valid @RequestBody StockInDto dto) {
        return Result.ok(stockInService.create(dto));
    }

    @PutMapping("/{id}/confirm")
    public Result<Void> confirm(@PathVariable Long id) {
        stockInService.confirm(id);
        return Result.ok();
    }

    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        stockInService.cancel(id);
        return Result.ok();
    }
}
