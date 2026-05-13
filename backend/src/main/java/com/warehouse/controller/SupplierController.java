package com.warehouse.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.common.Result;
import com.warehouse.entity.Supplier;
import com.warehouse.mapper.SupplierMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierMapper supplierMapper;

    @GetMapping
    public Result<Page<Supplier>> list(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer size,
                                        @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(Supplier::getSupplierName, keyword);
        }
        wrapper.orderByDesc(Supplier::getCreateTime);
        return Result.ok(supplierMapper.selectPage(new Page<>(page, size), wrapper));
    }

    @PostMapping
    public Result<Supplier> create(@RequestBody Supplier supplier) {
        supplierMapper.insert(supplier);
        return Result.ok(supplier);
    }

    @PutMapping("/{id}")
    public Result<Supplier> update(@PathVariable Long id, @RequestBody Supplier supplier) {
        supplier.setId(id);
        supplierMapper.updateById(supplier);
        return Result.ok(supplier);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        supplierMapper.deleteById(id);
        return Result.ok();
    }
}
