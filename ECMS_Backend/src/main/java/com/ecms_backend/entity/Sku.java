package com.ecms_backend.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Sku {
    private Long id;
    private String skuCode;
    private Long spuId;
    private String specInfo;
    private BigDecimal marketPrice;
    private BigDecimal salePrice;
    private BigDecimal memberPrice;
    private BigDecimal activityPrice;
    private LocalDateTime priceStartTime;
    private LocalDateTime priceEndTime;
    private Integer stock;
    private Integer lockedStock;
    private Integer warnThreshold;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
