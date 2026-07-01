package com.ecms_backend.controller;

import com.ecms_backend.common.ApiResult;
import com.ecms_backend.entity.Tag;
import com.ecms_backend.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping
    public ApiResult<List<Tag>> list() {
        try {
            List<Tag> tags = tagService.getAll();
            return ApiResult.success(tags);
        } catch (Exception e) {
            return ApiResult.error(500, e.getMessage());
        }
    }
}
