package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wms_inventory_log")
public class InventoryLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long goodsId;
    private Integer beforeQuantity;
    private Integer changeQuantity;
    private Integer afterQuantity;
    private String type; // IN / OUT / ADJUST
    private String bizNo;
    private Long operatorId;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
