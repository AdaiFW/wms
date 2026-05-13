package com.warehouse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class StockOutDto {
    @NotNull(message = "商品不能为空")
    private Long goodsId;
    @NotNull(message = "数量不能为空")
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    @NotBlank(message = "目标信息不能为空")
    private String targetInfo;
    private String remark;
}
