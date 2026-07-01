package com.ecms_backend.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PriceLog {
    private Long id;
    private Long skuId;
    private String priceType;
    private BigDecimal beforePrice;
    private BigDecimal afterPrice;
    private Long operatorId;
    private LocalDateTime createdAt;
}
