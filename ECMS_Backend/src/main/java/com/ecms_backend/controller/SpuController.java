package com.ecms_backend.controller;

import com.ecms_backend.common.ApiResult;
import com.ecms_backend.common.BatchResult;
import com.ecms_backend.common.PageResult;
import com.ecms_backend.entity.Sku;
import com.ecms_backend.entity.Spu;
import com.ecms_backend.mapper.SkuMapper;
import com.ecms_backend.service.SpuService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class SpuController {

    @Autowired
    private SpuService spuService;

    @Autowired
    private SkuMapper skuMapper;

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
    public ApiResult<String> batchOnShelf(@RequestBody Map<String, List<Long>> body) {
        try {
            List<Long> ids = body.get("ids");
            int successCount = spuService.batchOnShelf(ids);
            return ApiResult.success("批量上架完成，共 " + successCount + " 件商品");
        } catch (Exception e) {
            return ApiResult.error(500, e.getMessage());
        }
    }

    @PostMapping("/batch-off-shelf")
    public ApiResult<String> batchOffShelf(@RequestBody Map<String, List<Long>> body) {
        try {
            List<Long> ids = body.get("ids");
            int successCount = spuService.batchOffShelf(ids);
            return ApiResult.success("批量下架完成，共 " + successCount + " 件商品");
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

    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("offset", 0);
        params.put("limit", 1000);
        PageResult<Spu> result = spuService.queryPage(params);
        List<Spu> spuList = result.getRecords();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("商品列表");

            Row headerRow = sheet.createRow(0);
            String[] headers = { "SPU编码", "商品名称", "副标题", "分类", "品牌", "售价", "总库存", "状态", "创建时间" };
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            String[] statusLabels = { "草稿", "待审核", "已上架", "已下架", "审核驳回" };

            int rowNum = 1;
            for (Spu spu : spuList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(spu.getSpuCode() != null ? spu.getSpuCode() : "");
                row.createCell(1).setCellValue(spu.getSpuName() != null ? spu.getSpuName() : "");
                row.createCell(2).setCellValue(spu.getSubTitle() != null ? spu.getSubTitle() : "");
                row.createCell(3).setCellValue(spu.getCategoryName() != null ? spu.getCategoryName() : "");
                row.createCell(4).setCellValue(spu.getBrandName() != null ? spu.getBrandName() : "");
                row.createCell(5).setCellValue(spu.getMinPrice() != null ? spu.getMinPrice().toString() : "0");
                row.createCell(6).setCellValue(spu.getTotalStock() != null ? spu.getTotalStock() : 0);
                row.createCell(7)
                        .setCellValue(spu.getStatus() != null && spu.getStatus() < statusLabels.length
                                ? statusLabels[spu.getStatus()]
                                : "未知");
                row.createCell(8).setCellValue(spu.getCreatedAt() != null ? spu.getCreatedAt().toString() : "");
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            String filename = "商品列表_" + System.currentTimeMillis() + ".xlsx";
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition",
                    "attachment; filename=" + URLEncoder.encode(filename, StandardCharsets.UTF_8));
            workbook.write(response.getOutputStream());
        }
    }
}
