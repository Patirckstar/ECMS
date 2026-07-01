package com.ecms_backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Brand {
    private Long id;
    private String brandName;
    private String brandLogo;
    private String brandDesc;
    private Integer sortOrder;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
