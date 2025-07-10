package com.example.user_service.service;

import com.example.user_service.dto.user.UserResponseDto;
import com.example.user_service.entity.User;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.net.URI;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class StorageService {
    @Value("${s3.key_id}")
    private String keyId;

    @Value("${s3.secret_key}")
    private String secretKey;

    @Value("${s3.region}")
    private String region;

    @Value("${s3.endpoint}")
    private String endpoint;

    @Value("${s3.bucket}")
    private String bucket;

    private S3Client s3Client;

    private final UserServiceImpl userService;



    @PostConstruct
    public void init() {
        if (keyId == null || secretKey == null) {
            throw new IllegalStateException("S3 credentials are not set!");
        }

        AwsCredentials credentials = AwsBasicCredentials.create(keyId, secretKey);
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    public void uploadAvatar(Authentication authentication, MultipartFile file) throws IOException {

        User user = userService.loadUserByUsername(userService.getCurrentUser(authentication).username());

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(String.format("users/avatars/%d", user.getId()))
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
    }

    public String getUserAvatar(Authentication authentication){
        User user = userService.loadUserByUsername(userService.getCurrentUser(authentication).username());

        return getImageFromS3(user.getId());
    }

    public Map<String, String> getUsersAvatars(Long[] userIds) {
        Map<String, String> result = new HashMap<>();

        for (Long userId : userIds) {
            try {
                String imageUrl = getImageFromS3(userId);
                result.put(userId.toString(), imageUrl);
            } catch (Exception e) {
                log.error("Ошибка при загрузке изображения для пользователя {}", userId, e);
                result.put(userId.toString(), null);
            }
        }

        return result;
    }

    private String getImageFromS3(Long userId) {
        try {
            GetObjectRequest objectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(String.format("users/avatars/%d", userId))
                    .build();

            try (ResponseInputStream<GetObjectResponse> inputStream = s3Client.getObject(objectRequest)) {
                byte[] imageBytes = inputStream.readAllBytes();
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                String contentType = inputStream.response().contentType();
                return String.format("data:%s;base64,%s", contentType, base64Image);
            }
        } catch (NoSuchKeyException e) {
            log.warn("Изображение не найдено для пользователя {}", userId);
            return null;
        } catch (S3Exception | IOException e) {
            log.error("Ошибка S3 при загрузке изображения пользователя {}", userId, e);
            throw new RuntimeException("Ошибка загрузки изображения", e);
        }
    }

}
