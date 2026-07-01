package com.ecms_backend.service;

import com.ecms_backend.entity.Tag;
import com.ecms_backend.mapper.TagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {

    @Autowired
    private TagMapper tagMapper;

    public List<Tag> getAll() {
        return tagMapper.selectAll();
    }
}
