package com.ecms_backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AuditLog {
    private Long id;
    private Long spuId;
    private Integer auditResult;
    private Long auditorId;
    private String rejectReason;
    private LocalDateTime auditTime;
}
