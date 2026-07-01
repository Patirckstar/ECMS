package com.ecms_backend.service;

import com.ecms_backend.entity.Category;
import com.ecms_backend.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    public List<Category> getAll() {
        return categoryMapper.selectAll();
    }

    public List<Category> getTree() {
        List<Category> all = categoryMapper.selectAll();
        Map<Long, Category> map = new HashMap<>();
        for (Category cat : all) {
            map.put(cat.getId(), cat);
        }

        List<Category> tree = new ArrayList<>();
        for (Category cat : all) {
            if (cat.getParentId() == null || cat.getParentId() == 0) {
                tree.add(cat);
            } else {
                Category parent = map.get(cat.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(cat);
                }
            }
        }
        return tree;
    }
}
