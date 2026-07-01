package com.ecms_backend.controller;

import com.ecms_backend.common.ApiResult;
import com.ecms_backend.entity.Category;
import com.ecms_backend.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/tree")
    public ApiResult<List<Category>> getTree() {
        try {
            List<Category> tree = categoryService.getTree();
            return ApiResult.success(tree);
        } catch (Exception e) {
            return ApiResult.error(500, e.getMessage());
        }
    }

    @GetMapping
    public ApiResult<List<Category>> list(@RequestParam(required = false) Integer level) {
        try {
            List<Category> list = categoryService.getAll();
            return ApiResult.success(list);
        } catch (Exception e) {
            return ApiResult.error(500, e.getMessage());
        }
    }
}
