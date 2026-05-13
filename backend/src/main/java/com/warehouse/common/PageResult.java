package com.warehouse.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    private Long total;
    private List<T> records;
    private Long page;
    private Long size;

    public static <T> PageResult<T> of(Long total, List<T> records) {
        return new PageResult<>(total, records, null, null);
    }
}
