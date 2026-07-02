package com.ecms_backend.controller;

import com.ecms_backend.common.ApiResult;
import com.ecms_backend.service.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Autowired
    private OssService ossService;

    @PostMapping("/image")
    public ApiResult<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ApiResult.error(400, "请选择要上传的文件");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !isValidImage(originalFilename)) {
            return ApiResult.error(400, "不支持的文件格式，仅支持PNG、JPG、GIF、WebP格式");
        }

        try {
            String publicUrl = ossService.upload(file, "images");

            Map<String, Object> result = new HashMap<>();
            result.put("url", publicUrl);
            result.put("filename", publicUrl.substring(publicUrl.lastIndexOf("/") + 1));
            result.put("originalFilename", originalFilename);
            result.put("size", file.getSize());

            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error(500, "文件上传失败：" + e.getMessage());
        }
    }

    @PostMapping("/video")
    public ApiResult<Map<String, Object>> uploadVideo(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ApiResult.error(400, "请选择要上传的文件");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !isValidVideo(originalFilename)) {
            return ApiResult.error(400, "不支持的文件格式，仅支持MP4、AVI、MOV格式");
        }

        try {
            String publicUrl = ossService.upload(file, "videos");

            Map<String, Object> result = new HashMap<>();
            result.put("url", publicUrl);
            result.put("filename", publicUrl.substring(publicUrl.lastIndexOf("/") + 1));
            result.put("originalFilename", originalFilename);
            result.put("size", file.getSize());

            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error(500, "文件上传失败：" + e.getMessage());
        }
    }

    private boolean isValidImage(String filename) {
        String lowerName = filename.toLowerCase();
        return lowerName.endsWith(".png") || lowerName.endsWith(".jpg") ||
                lowerName.endsWith(".jpeg") || lowerName.endsWith(".gif") ||
                lowerName.endsWith(".webp");
    }

    private boolean isValidVideo(String filename) {
        String lowerName = filename.toLowerCase();
        return lowerName.endsWith(".mp4") || lowerName.endsWith(".avi") ||
                lowerName.endsWith(".mov") || lowerName.endsWith(".mkv");
    }
}
