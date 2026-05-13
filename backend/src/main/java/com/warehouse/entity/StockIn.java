package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("wms_stock_in")
public class StockIn {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String stockInNo;
    private Long goodsId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private Long supplierId;
    private Long operatorId;
    private String remark;
    private Integer status; // 0-草稿 1-已入库 2-已取消
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
