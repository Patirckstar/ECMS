package com.ecms_backend.controller;

import com.ecms_backend.common.ApiResult;
import com.ecms_backend.entity.Brand;
import com.ecms_backend.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @GetMapping
    public ApiResult<List<Brand>> list() {
        try {
            List<Brand> brands = brandService.getAll();
            return ApiResult.success(brands);
        } catch (Exception e) {
            return ApiResult.error(500, e.getMessage());
        }
    }
}
