package com.ecms_backend.controller;

import com.ecms_backend.common.ApiResult;
import com.ecms_backend.entity.SpecTemplate;
import com.ecms_backend.entity.SpecValue;
import com.ecms_backend.mapper.SpecTemplateMapper;
import com.ecms_backend.mapper.SpecValueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/specs")
public class SpecController {

    @Autowired
    private SpecTemplateMapper specTemplateMapper;

    @Autowired
    private SpecValueMapper specValueMapper;

    @GetMapping("/templates")
    public ApiResult<List<SpecTemplate>> getTemplates() {
        try {
            List<SpecTemplate> list = specTemplateMapper.selectAll();
            return ApiResult.success(list);
        } catch (Exception e) {
            return ApiResult.error(500, e.getMessage());
        }
    }

    @GetMapping("/templates/{id}")
    public ApiResult<SpecTemplate> getTemplate(@PathVariable Long id) {
        try {
            SpecTemplate template = specTemplateMapper.selectById(id);
            if (template != null) {
                template.setValues(specValueMapper.selectBySpecId(id));
            }
            return ApiResult.success(template);
        } catch (Exception e) {
            return ApiResult.error(500, e.getMessage());
        }
    }

    @PostMapping("/templates")
    public ApiResult<SpecTemplate> createTemplate(@RequestBody SpecTemplate template) {
        try {
            template.setStatus(1);
            template.setCreatedAt(LocalDateTime.now());
            specTemplateMapper.insert(template);

            if (template.getValues() != null && !template.getValues().isEmpty()) {
                for (int i = 0; i < template.getValues().size(); i++) {
                    SpecValue value = template.getValues().get(i);
                    value.setSpecId(template.getId());
                    value.setSortOrder(i);
                    value.setCreatedAt(LocalDateTime.now());
                    specValueMapper.insert(value);
                }
            }
            return ApiResult.success(template);
        } catch (Exception e) {
            return ApiResult.error(500, e.getMessage());
        }
    }

    @PutMapping("/templates/{id}")
    public ApiResult<SpecTemplate> updateTemplate(@PathVariable Long id, @RequestBody SpecTemplate template) {
        try {
            template.setId(id);
            template.setUpdatedAt(LocalDateTime.now());
            specTemplateMapper.update(template);

            if (template.getValues() != null) {
                specValueMapper.deleteBySpecId(id);
                for (int i = 0; i < template.getValues().size(); i++) {
                    SpecValue value = template.getValues().get(i);
                    value.setSpecId(id);
                    value.setSortOrder(i);
                    value.setCreatedAt(LocalDateTime.now());
                    specValueMapper.insert(value);
                }
            }
            return ApiResult.success(template);
        } catch (Exception e) {
            return ApiResult.error(500, e.getMessage());
        }
    }

    @DeleteMapping("/templates/{id}")
    public ApiResult<Void> deleteTemplate(@PathVariable Long id) {
        try {
            specTemplateMapper.deleteById(id);
            specValueMapper.deleteBySpecId(id);
            return ApiResult.success(null);
        } catch (Exception e) {
            return ApiResult.error(500, e.getMessage());
        }
    }

    @GetMapping("/values/{specId}")
    public ApiResult<List<SpecValue>> getValuesBySpecId(@PathVariable Long specId) {
        try {
            List<SpecValue> list = specValueMapper.selectBySpecId(specId);
            return ApiResult.success(list);
        } catch (Exception e) {
            return ApiResult.error(500, e.getMessage());
        }
    }

    @PostMapping("/values")
    public ApiResult<SpecValue> createValue(@RequestBody SpecValue value) {
        try {
            value.setCreatedAt(LocalDateTime.now());
            specValueMapper.insert(value);
            return ApiResult.success(value);
        } catch (Exception e) {
            return ApiResult.error(500, e.getMessage());
        }
    }

    @PutMapping("/values/{id}")
    public ApiResult<SpecValue> updateValue(@PathVariable Long id, @RequestBody SpecValue value) {
        try {
            value.setId(id);
            specValueMapper.update(value);
            return ApiResult.success(value);
        } catch (Exception e) {
            return ApiResult.error(500, e.getMessage());
        }
    }

    @DeleteMapping("/values/{id}")
    public ApiResult<Void> deleteValue(@PathVariable Long id) {
        try {
            specValueMapper.deleteById(id);
            return ApiResult.success(null);
        } catch (Exception e) {
            return ApiResult.error(500, e.getMessage());
        }
    }
}