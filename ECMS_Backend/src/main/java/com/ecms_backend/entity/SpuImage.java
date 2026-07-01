package com.ecms_backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SpuImage {
    private Long id;
    private Long spuId;
    private Integer imageType;
    private String imageUrl;
    private String videoUrl;
    private Integer sortOrder;
    private Integer isCover;
    private LocalDateTime createdAt;
}
