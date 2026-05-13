package com.warehouse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GoodsDto {
    @NotBlank(message = "商品编码不能为空")
    private String goodsCode;
    @NotBlank(message = "商品名称不能为空")
    private String goodsName;
    @NotNull(message = "分类不能为空")
    private Long categoryId;
    private String unit;
    private String spec;
    private BigDecimal price;
    private String imageUrl;
    private String description;
}
