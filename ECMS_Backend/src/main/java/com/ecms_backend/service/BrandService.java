package com.ecms_backend.service;

import com.ecms_backend.entity.Brand;
import com.ecms_backend.mapper.BrandMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandMapper brandMapper;

    public List<Brand> getAll() {
        return brandMapper.selectAll();
    }
}
