package com.ecms_backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StatusLog {
    private Long id;
    private Long spuId;
    private Integer fromStatus;
    private Integer toStatus;
    private Long operatorId;
    private String remark;
    private LocalDateTime createdAt;
}
