package com.ecms_backend.controller;

import com.ecms_backend.common.ApiResult;
import com.ecms_backend.entity.Sku;
import com.ecms_backend.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    @PostMapping
    public ApiResult<Sku> create(@PathVariable Long spuId, @RequestBody Sku sku) {
        try {
            Sku created = skuService.create(spuId, sku);
            return ApiResult.success(created);
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
            List<Map<String, Object>> rawSkus = (List<Map<String, Object>>) body.get("skus");
            List<Sku> skus = new ArrayList<>();
            for (Map<String, Object> raw : rawSkus) {
                Sku sku = new Sku();
                if (raw.containsKey("id")) {
                    sku.setId(((Number) raw.get("id")).longValue());
                }
                if (raw.containsKey("salePrice")) {
                    sku.setSalePrice(new java.math.BigDecimal(raw.get("salePrice").toString()));
                }
                if (raw.containsKey("marketPrice")) {
                    sku.setMarketPrice(new java.math.BigDecimal(raw.get("marketPrice").toString()));
                }
                if (raw.containsKey("memberPrice")) {
                    sku.setMemberPrice(new java.math.BigDecimal(raw.get("memberPrice").toString()));
                }
                if (raw.containsKey("stock")) {
                    sku.setStock(((Number) raw.get("stock")).intValue());
                }
                if (raw.containsKey("warnThreshold")) {
                    sku.setWarnThreshold(((Number) raw.get("warnThreshold")).intValue());
                }
                if (raw.containsKey("status")) {
                    sku.setStatus(((Number) raw.get("status")).intValue());
                }
                skus.add(sku);
            }
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
