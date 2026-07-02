package com.ecms_backend.common;

import lombok.Data;

@Data
public class BatchResult {
    private int successCount;
    private int totalCount;

    public BatchResult(int successCount, int totalCount) {
        this.successCount = successCount;
        this.totalCount = totalCount;
    }
}