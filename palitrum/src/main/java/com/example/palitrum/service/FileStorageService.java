package com.example.palitrum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final S3Client s3;

    @Value("${yandex.cloud.bucket-name}")
    private String bucket;

    public record UploadResult(String key, String url, long size, String contentType, String originalFilename) {}

    public UploadResult upload(MultipartFile file) throws IOException {
        String key = UUID.randomUUID() + "_" + file.getOriginalFilename();

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3.putObject(request, RequestBody.fromBytes(file.getBytes()));

        String url = "https://storage.yandexcloud.net/" + bucket + "/" + key;
        return new UploadResult(key, url, file.getSize(), file.getContentType(), file.getOriginalFilename());
    }

    public void delete(String key) {
        if (key == null || key.isBlank()) return;
        DeleteObjectRequest req = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        try {
            s3.deleteObject(req);
        } catch (Exception ex) {
            System.err.println("Failed to delete S3 object " + key + " : " + ex.getMessage());
        }
    }


    public String extractKeyFromUrl(String url) {
        // example: https://storage.yandexcloud.net/palitrumbacket/uuid_filename
        String[] parts = url.split("/");
        return parts[parts.length - 1];
    }
}