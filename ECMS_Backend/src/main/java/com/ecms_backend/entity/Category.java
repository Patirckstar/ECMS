package com.ecms_backend.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Category {
    private Long id;
    private Long parentId;
    private Integer level;
    private String catName;
    private String catIcon;
    private Integer sortOrder;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Category> children;
}
