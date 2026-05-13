package com.warehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.warehouse.entity.Inventory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface InventoryMapper extends BaseMapper<Inventory> {

    @Select("SELECT i.*, g.goods_name, g.goods_code, g.unit, c.category_name " +
            "FROM wms_inventory i " +
            "LEFT JOIN wms_goods g ON i.goods_id = g.id " +
            "LEFT JOIN wms_category c ON g.category_id = c.id " +
            "WHERE i.deleted = 0 " +
            "ORDER BY i.update_time DESC")
    List<Map<String, Object>> selectInventoryWithGoods();

    @Select("SELECT i.*, g.goods_name, g.goods_code, g.unit " +
            "FROM wms_inventory i " +
            "LEFT JOIN wms_goods g ON i.goods_id = g.id " +
            "WHERE i.deleted = 0 AND i.quantity <= i.alert_threshold")
    List<Map<String, Object>> selectAlertInventories(@Param("threshold") Integer threshold);
}
