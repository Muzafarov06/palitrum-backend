package com.example.palitrum.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
public class CosConfig {

    @Bean
    public S3Client s3Client(
            @Value("${yandex.cloud.access-key}") String accessKey,
            @Value("${yandex.cloud.secret-key}") String secretKey,
            @Value("${yandex.cloud.endpoint}") String endpoint
    ) {
        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                )
                .region(Region.of("ru-central1"))
                .serviceConfiguration(S3Configuration.builder().build())
                .build();
    }
}
