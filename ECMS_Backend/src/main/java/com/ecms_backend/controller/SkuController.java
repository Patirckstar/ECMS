package com.ecms_backend.controller;

import com.ecms_backend.common.ApiResult;
import com.ecms_backend.entity.Sku;
import com.ecms_backend.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products/{spuId}/skus")
public class SkuController {

    @Autowired
    private SkuService skuService;

    @GetMapping
    public ApiResult<List<Sku>> listBySpuId(@PathVariable Long spuId) {
        try {
            List<Sku> skus = skuService.getBySpuId(spuId);
            return ApiResult.success(skus);
        } catch (Exception e) {
            return ApiResult.error(500, e.getMessage());
        }
    }

    @PutMapping("/{skuId}")
    public ApiResult<Sku> update(@PathVariable Long spuId, @PathVariable Long skuId, @RequestBody Sku sku) {
        try {
            Sku updated = skuService.update(spuId, skuId, sku);
            return ApiResult.success(updated);
        } catch (Exception e) {
            return ApiResult.error(500, e.getMessage());
        }
    }

    @PutMapping("/batch")
    public ApiResult<Void> batchUpdate(@PathVariable Long spuId, @RequestBody Map<String, Object> body) {
        try {
            @SuppressWarnings("unchecked")
            List<Sku> skus = (List<Sku>) body.get("skus");
            skuService.batchUpdate(spuId, skus);
            return ApiResult.success(null);
        } catch (Exception e) {
            return ApiResult.error(500, e.getMessage());
        }
    }

    @PostMapping("/{skuId}/adjust-stock")
    public ApiResult<Void> adjustStock(@PathVariable Long spuId, @PathVariable Long skuId,
            @RequestBody Map<String, Object> body) {
        try {
            Object qtyObj = body.get("qty");
            if (qtyObj == null) {
                return ApiResult.error(400, "调整数量不能为空");
            }
            int qty = ((Number) qtyObj).intValue();
            String remark = (String) body.get("remark");
            skuService.adjustStock(spuId, skuId, qty, remark);
            return ApiResult.success(null);
        } catch (Exception e) {
            return ApiResult.error(500, e.getMessage());
        }
    }

    @PostMapping("/batch-adjust-stock")
    public ApiResult<Void> batchAdjustStock(@PathVariable Long spuId, @RequestBody Map<String, Object> body) {
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");
            skuService.batchAdjustStock(spuId, items);
            return ApiResult.success(null);
        } catch (Exception e) {
            return ApiResult.error(500, e.getMessage());
        }
    }
}
