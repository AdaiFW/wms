package com.warehouse.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.dto.StockInDto;
import com.warehouse.entity.StockIn;

public interface StockInService {
    Page<StockIn> page(Integer pageNum, Integer pageSize, String keyword);
    StockIn getById(Long id);
    StockIn create(StockInDto dto);
    void confirm(Long id);
    void cancel(Long id);
}
