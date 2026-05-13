package com.warehouse.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class StockInDto {
    @NotNull(message = "商品不能为空")
    private Long goodsId;
    @NotNull(message = "数量不能为空")
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private Long supplierId;
    private String remark;
}
