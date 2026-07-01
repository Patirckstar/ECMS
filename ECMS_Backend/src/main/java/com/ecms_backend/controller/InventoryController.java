package com.ecms_backend.controller;

import com.ecms_backend.common.ApiResult;
import com.ecms_backend.common.PageResult;
import com.ecms_backend.entity.AuditLog;
import com.ecms_backend.entity.InventoryLog;
import com.ecms_backend.entity.Sku;
import com.ecms_backend.entity.StatusLog;
import com.ecms_backend.service.InventoryLogService;
import com.ecms_backend.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/{spuId}")
public class InventoryController {

    @Autowired
    private InventoryLogService inventoryLogService;

    @Autowired
    private SkuService skuService;

    @GetMapping("/inventory")
    public ApiResult<List<Sku>> getInventory(@PathVariable Long spuId) {
        try {
            List<Sku> skus = skuService.getBySpuId(spuId);
            return ApiResult.success(skus);
        } catch (Exception e) {
            return ApiResult.error(500, e.getMessage());
        }
    }

    @GetMapping("/inventory-logs")
    public ApiResult<PageResult<InventoryLog>> getInventoryLogs(
            @PathVariable Long spuId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Integer changeType) {
        try {
            PageResult<InventoryLog> result = inventoryLogService.getBySpuId(spuId, changeType, page, pageSize);
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error(500, e.getMessage());
        }
    }

    @GetMapping("/status-logs")
    public ApiResult<List<StatusLog>> getStatusLogs(@PathVariable Long spuId) {
        try {
            List<StatusLog> logs = inventoryLogService.getStatusLogs(spuId);
            return ApiResult.success(logs);
        } catch (Exception e) {
            return ApiResult.error(500, e.getMessage());
        }
    }

    @GetMapping("/audit-logs")
    public ApiResult<List<AuditLog>> getAuditLogs(@PathVariable Long spuId) {
        try {
            List<AuditLog> logs = inventoryLogService.getAuditLogs(spuId);
            return ApiResult.success(logs);
        } catch (Exception e) {
            return ApiResult.error(500, e.getMessage());
        }
    }
}
