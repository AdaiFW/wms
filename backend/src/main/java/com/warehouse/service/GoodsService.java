package com.warehouse.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.dto.GoodsDto;
import com.warehouse.entity.Goods;

public interface GoodsService {
    Page<Goods> page(Integer pageNum, Integer pageSize, String keyword);
    Goods getById(Long id);
    Goods create(GoodsDto dto);
    Goods update(Long id, GoodsDto dto);
    void delete(Long id);
}
