package com.ecms_backend.common;

import lombok.Data;

/**
 * 分页结果
 */
@Data
public class PageResult<T> {
    private java.util.List<T> records;
    private long total;
    private int page;
    private int pageSize;

    public static <T> PageResult<T> of(java.util.List<T> records, long total, int page, int pageSize) {
        PageResult<T> result = new PageResult<>();
        result.records = records;
        result.total = total;
        result.page = page;
        result.pageSize = pageSize;
        return result;
    }
}
