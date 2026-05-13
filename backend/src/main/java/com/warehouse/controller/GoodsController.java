package com.warehouse.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.common.Result;
import com.warehouse.dto.GoodsDto;
import com.warehouse.entity.Goods;
import com.warehouse.service.GoodsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/goods")
@RequiredArgsConstructor
public class GoodsController {

    private final GoodsService goodsService;

    @GetMapping
    public Result<Page<Goods>> list(@RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "10") Integer size,
                                     @RequestParam(required = false) String keyword) {
        return Result.ok(goodsService.page(page, size, keyword));
    }

    @GetMapping("/{id}")
    public Result<Goods> get(@PathVariable Long id) {
        return Result.ok(goodsService.getById(id));
    }

    @PostMapping
    public Result<Goods> create(@Valid @RequestBody GoodsDto dto) {
        return Result.ok(goodsService.create(dto));
    }

    @PutMapping("/{id}")
    public Result<Goods> update(@PathVariable Long id, @Valid @RequestBody GoodsDto dto) {
        return Result.ok(goodsService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        goodsService.delete(id);
        return Result.ok();
    }
}
