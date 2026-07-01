package com.ecms_backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SpecValue {
    private Long id;
    private Long specId;
    private String valueName;
    private Integer sortOrder;
    private LocalDateTime createdAt;
}
