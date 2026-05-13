package com.warehouse.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.dto.InventoryUpdateDto;
import com.warehouse.entity.Inventory;

import java.util.List;
import java.util.Map;

public interface InventoryService {
    Page<Map<String, Object>> page(Integer pageNum, Integer pageSize, String keyword);
    Inventory getByGoodsId(Long goodsId);
    void updateConfig(InventoryUpdateDto dto);
    List<Map<String, Object>> getAlerts();
    Map<String, Object> getOverview();
}
