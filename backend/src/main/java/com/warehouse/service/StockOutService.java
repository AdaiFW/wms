package com.warehouse.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.dto.StockOutDto;
import com.warehouse.entity.StockOut;

public interface StockOutService {
    Page<StockOut> page(Integer pageNum, Integer pageSize, String keyword);
    StockOut getById(Long id);
    StockOut create(StockOutDto dto);
    void confirm(Long id);
    void cancel(Long id);
}
