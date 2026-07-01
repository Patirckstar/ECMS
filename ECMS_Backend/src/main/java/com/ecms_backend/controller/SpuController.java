package com.ecms_backend.controller;

import com.ecms_backend.common.ApiResult;
import com.ecms_backend.common.PageResult;
import com.ecms_backend.entity.Spu;
import com.ecms_backend.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class SpuController {

    @Autowired
    private SpuService spuService;

    @GetMapping
    public ApiResult<PageResult<Spu>> queryPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false) Double priceMin,
            @RequestParam(required = false) Double priceMax,
            @RequestParam(required = false) Integer stockMin,
            @RequestParam(required = false) Integer stockMax,
            @RequestParam(required = false) String createTimeStart,
            @RequestParam(required = false) String createTimeEnd) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("page", page);
            params.put("pageSize", pageSize);
            if (keyword != null && !keyword.isEmpty())
                params.put("keyword", keyword);
            if (status != null)
                params.put("status", status);
            if (categoryId != null)
                params.put("categoryId", categoryId);
            if (tagId != null)
                params.put("tagId", tagId);
            if (priceMin != null)
                params.put("priceMin", priceMin);
            if (priceMax != null)
                params.put("priceMax", priceMax);
            if (stockMin != null)
                params.put("stockMin", stockMin);
            if (stockMax != null)
                params.put("stockMax", stockMax);
            if (createTimeStart != null)
                params.put("createTimeStart", createTimeStart);
            if (createTimeEnd != null)
                params.put("createTimeEnd", createTimeEnd);

            PageResult<Spu> result = spuService.queryPage(params);
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error(500, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResult<Spu> getDetail(@PathVariable Long id) {
        try {
            Spu spu = spuService.getById(id);
            return ApiResult.success(spu);
        } catch (Exception e) {
            return ApiResult.error(500, e.getMessage());
        }
    }

    @PostMapping
    public ApiResult<Spu> create(@RequestBody Spu spu) {
        try {
            Spu created = spuService.create(spu);
            return ApiResult.success(created);
        } catch (Exception e) {
            return ApiResult.error(500, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResult<Spu> update(@PathVariable Long id, @RequestBody Spu spu) {
        try {
            Spu updated = spuService.update(id, spu);
            return ApiResult.success(updated);
        } catch (Exception e) {
            return ApiResult.error(500, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        try {
            spuService.delete(id);
            return ApiResult.success(null);
        } catch (Exception e) {
            return ApiResult.error(500, e.getMessage());
        }
    }

    @PutMapping("/{id}/on-shelf")
    public ApiResult<Void> onShelf(@PathVariable Long id) {
        try {
            spuService.onShelf(id);
            return ApiResult.success(null);
        } catch (Exception e) {
            return ApiResult.error(500, e.getMessage());
        }
    }

    @PutMapping("/{id}/off-shelf")
    public ApiResult<Void> offShelf(@PathVariable Long id) {
        try {
            spuService.offShelf(id);
            return ApiResult.success(null);
        } catch (Exception e) {
            return ApiResult.error(500, e.getMessage());
        }
    }

    @PostMapping("/batch-on-shelf")
    public ApiResult<Void> batchOnShelf(@RequestBody Map<String, List<Long>> body) {
        try {
            List<Long> ids = body.get("ids");
            spuService.batchOnShelf(ids);
            return ApiResult.success(null);
        } catch (Exception e) {
            return ApiResult.error(500, e.getMessage());
        }
    }

    @PostMapping("/batch-off-shelf")
    public ApiResult<Void> batchOffShelf(@RequestBody Map<String, List<Long>> body) {
        try {
            List<Long> ids = body.get("ids");
            spuService.batchOffShelf(ids);
            return ApiResult.success(null);
        } catch (Exception e) {
            return ApiResult.error(500, e.getMessage());
        }
    }

    @PostMapping("/batch-delete")
    public ApiResult<Void> batchDelete(@RequestBody Map<String, List<Long>> body) {
        try {
            List<Long> ids = body.get("ids");
            spuService.batchDelete(ids);
            return ApiResult.success(null);
        } catch (Exception e) {
            return ApiResult.error(500, e.getMessage());
        }
    }

    @PutMapping("/{id}/submit-audit")
    public ApiResult<Void> submitAudit(@PathVariable Long id) {
        try {
            spuService.submitAudit(id);
            return ApiResult.success(null);
        } catch (Exception e) {
            return ApiResult.error(500, e.getMessage());
        }
    }

    @PutMapping("/{id}/audit/approve")
    public ApiResult<Void> auditApprove(@PathVariable Long id) {
        try {
            spuService.approve(id, 1L);
            return ApiResult.success(null);
        } catch (Exception e) {
            return ApiResult.error(500, e.getMessage());
        }
    }

    @PutMapping("/{id}/audit/reject")
    public ApiResult<Void> auditReject(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String rejectReason = body.get("rejectReason");
            spuService.reject(id, 1L, rejectReason);
            return ApiResult.success(null);
        } catch (Exception e) {
            return ApiResult.error(500, e.getMessage());
        }
    }
}
