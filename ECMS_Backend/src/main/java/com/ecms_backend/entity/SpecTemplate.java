package com.ecms_backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SpecTemplate {
    private Long id;
    private String specName;
    private Integer sortOrder;
    private Integer status;
    private LocalDateTime createdAt;
}
