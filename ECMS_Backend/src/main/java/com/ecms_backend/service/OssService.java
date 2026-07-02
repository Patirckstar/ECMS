package com.ecms_backend.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class OssService {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    @Value("${aliyun.oss.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.oss.access-key-secret}")
    private String accessKeySecret;

    private OSS ossClient;

    @PostConstruct
    public void init() {
        ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    /**
     * 上传文件到 OSS
     *
     * @param file     上传的文件
     * @param fileType 文件类型：images 或 videos
     * @return 可直接访问的公开 URL
     */
    public String upload(MultipartFile file, String fileType) {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFilename = UUID.randomUUID().toString().replace("-", "") + extension;
        String objectKey = "ECMS_resources/" + fileType + "/" + newFilename;

        try (InputStream inputStream = file.getInputStream()) {
            PutObjectRequest putRequest = new PutObjectRequest(bucketName, objectKey, inputStream);
            ossClient.putObject(putRequest);
            return getPublicUrl(objectKey);
        } catch (IOException e) {
            throw new RuntimeException("OSS 上传失败：" + e.getMessage(), e);
        }
    }

    /**
     * 根据 objectKey 获取公开访问 URL（Bucket 需设为公共读）
     */
    public String getPublicUrl(String objectKey) {
        String domain = endpoint.replace("https://", "https://" + bucketName + ".");
        return domain + "/" + objectKey;
    }

    /**
     * 获取 OSS 文件输入流
     */
    public InputStream getFileInputStream(String objectKey) {
        return ossClient.getObject(bucketName, objectKey).getObjectContent();
    }
}
