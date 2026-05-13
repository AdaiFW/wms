package com.warehouse.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InventoryUpdateDto {
    @NotNull(message = "商品不能为空")
    private Long goodsId;
    private Integer minStock;
    private Integer maxStock;
    @NotNull(message = "预警阈值不能为空")
    private Integer alertThreshold;
}
