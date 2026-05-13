package com.warehouse.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.common.Result;
import com.warehouse.dto.StockOutDto;
import com.warehouse.entity.StockOut;
import com.warehouse.service.StockOutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stock-out")
@RequiredArgsConstructor
public class StockOutController {

    private final StockOutService stockOutService;

    @GetMapping
    public Result<Page<StockOut>> list(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer size,
                                        @RequestParam(required = false) String keyword) {
        return Result.ok(stockOutService.page(page, size, keyword));
    }

    @GetMapping("/{id}")
    public Result<StockOut> get(@PathVariable Long id) {
        return Result.ok(stockOutService.getById(id));
    }

    @PostMapping
    public Result<StockOut> create(@Valid @RequestBody StockOutDto dto) {
        return Result.ok(stockOutService.create(dto));
    }

    @PutMapping("/{id}/confirm")
    public Result<Void> confirm(@PathVariable Long id) {
        stockOutService.confirm(id);
        return Result.ok();
    }

    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        stockOutService.cancel(id);
        return Result.ok();
    }
}
