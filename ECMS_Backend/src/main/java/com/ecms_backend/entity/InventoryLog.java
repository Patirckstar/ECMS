package com.ecms_backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class InventoryLog {
    private Long id;
    private Long skuId;
    private Long spuId;
    private Integer changeType;
    private Integer changeQty;
    private Integer beforeStock;
    private Integer afterStock;
    private Long operatorId;
    private String remark;
    private LocalDateTime createdAt;
}
