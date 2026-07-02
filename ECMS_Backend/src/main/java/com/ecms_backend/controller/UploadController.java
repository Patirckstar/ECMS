package com.ecms_backend.controller;

import com.ecms_backend.common.ApiResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private static final String UPLOAD_DIR = "./uploads/";

    @PostMapping("/image")
    public ApiResult<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ApiResult.error(400, "请选择要上传的文件");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !isValidImage(originalFilename)) {
            return ApiResult.error(400, "不支持的文件格式，仅支持PNG、JPG、GIF、WebP格式");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = UUID.randomUUID().toString().replace("-", "") + extension;

        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String uploadPath = UPLOAD_DIR + "images/" + datePath;
        String filePath = uploadPath + "/" + newFilename;

        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            file.transferTo(new File(filePath));
            String url = "/uploads/images/" + datePath + "/" + newFilename;

            Map<String, Object> result = new HashMap<>();
            result.put("url", url);
            result.put("filename", newFilename);
            result.put("originalFilename", originalFilename);
            result.put("size", file.getSize());

            return ApiResult.success(result);
        } catch (IOException e) {
            return ApiResult.error(500, "文件上传失败：" + e.getMessage());
        }
    }

    private boolean isValidImage(String filename) {
        String lowerName = filename.toLowerCase();
        return lowerName.endsWith(".png") || lowerName.endsWith(".jpg") || 
               lowerName.endsWith(".jpeg") || lowerName.endsWith(".gif") || 
               lowerName.endsWith(".webp");
    }
}