package com.ecms_backend.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SpecTemplate {
    private Long id;
    private String specName;
    private Integer sortOrder;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<SpecValue> values;
}