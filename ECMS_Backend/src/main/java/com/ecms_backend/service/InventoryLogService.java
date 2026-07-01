package com.ecms_backend.service;

import com.ecms_backend.common.PageResult;
import com.ecms_backend.entity.AuditLog;
import com.ecms_backend.entity.InventoryLog;
import com.ecms_backend.entity.StatusLog;
import com.ecms_backend.mapper.AuditLogMapper;
import com.ecms_backend.mapper.InventoryLogMapper;
import com.ecms_backend.mapper.StatusLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryLogService {

    @Autowired
    private InventoryLogMapper inventoryLogMapper;

    @Autowired
    private StatusLogMapper statusLogMapper;

    @Autowired
    private AuditLogMapper auditLogMapper;

    public PageResult<InventoryLog> getBySpuId(Long spuId, Integer changeType, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<InventoryLog> list = inventoryLogMapper.selectBySpuId(spuId, changeType, offset, pageSize);
        long total = inventoryLogMapper.countBySpuId(spuId, changeType);
        return PageResult.of(list, total, page, pageSize);
    }

    public List<StatusLog> getStatusLogs(Long spuId) {
        return statusLogMapper.selectBySpuId(spuId);
    }

    public List<AuditLog> getAuditLogs(Long spuId) {
        return auditLogMapper.selectBySpuId(spuId);
    }
}
