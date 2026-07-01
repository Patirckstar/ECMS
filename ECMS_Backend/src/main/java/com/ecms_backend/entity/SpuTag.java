package com.ecms_backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SpuTag {
    private Long id;
    private Long spuId;
    private Long tagId;
    private LocalDateTime createdAt;
}
