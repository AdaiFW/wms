package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("wms_goods")
public class Goods {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String goodsCode;
    private String goodsName;
    private Long categoryId;
    private String unit;
    private String spec;
    private BigDecimal price;
    private String imageUrl;
    private String description;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
