package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wms_inventory")
public class Inventory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long goodsId;
    private Integer quantity;
    private Integer minStock;
    private Integer maxStock;
    private Integer alertThreshold;
    @Version
    private Integer version;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
