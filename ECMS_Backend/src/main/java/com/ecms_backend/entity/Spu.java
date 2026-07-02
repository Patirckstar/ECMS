package com.ecms_backend.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Spu {
    private Long id;
    private String spuCode;
    private String spuName;
    private String subTitle;
    private String description;
    private Long brandId;
    private Long categoryId;
    private Integer productType;
    private Integer status;
    private Integer isDeleted;
    private LocalDateTime deletedAt;
    private Integer sevenDayReturn;
    private String warrantyDesc;
    private String returnPolicy;
    private Long freightTemplateId;
    private Integer isFreeShipping;
    private String shipFrom;
    private Integer shipHours;
    private Integer auditStatus;
    private Long auditorId;
    private LocalDateTime auditTime;
    private String rejectReason;
    private Integer autoOffline;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 前台表单传入的价格字段（仅用于创建默认SKU，不在spu表中持久化）
    private BigDecimal marketPrice;
    private BigDecimal salePrice;
    private BigDecimal memberPrice;

    private List<SpuImage> images;
    private List<Tag> tags;
    private List<Sku> skus;

    private String brandName;
    private String categoryName;
    private BigDecimal minPrice;
    private Integer totalStock;
    private Integer totalSales;
}
