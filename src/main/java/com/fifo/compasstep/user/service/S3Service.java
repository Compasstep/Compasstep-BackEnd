package com.fifo.compasstep.user.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.fifo.compasstep.user.dto.UserRequestDTO;
import com.fifo.compasstep.user.dto.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {
    private final AmazonS3 s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public UserResponseDTO.generatePresignedUrlResponseDTO generatePresignedUrl(
            UserRequestDTO.generatePresignedUrlRequestDTO request
    ){
        String originalFileName = request.getOriginalFileName();
        String folderType = request.getFileType();
        String contentType = request.getContentType();

        // 파일 키 구조: {폴더타입}/{UUID}_{파일이름}
        String fileKey = folderType + "/" + UUID.randomUUID().toString() + "_" + originalFileName;

        Date expiration = new Date();
        expiration.setTime(expiration.getTime() + 24 * 60 * 60 * 1000); //24시간으로 박긴 했는데 논의해봐야할듯

        GeneratePresignedUrlRequest presignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, fileKey)
                        .withMethod(com.amazonaws.HttpMethod.PUT)
                        .withExpiration(expiration)
                        .withContentType(contentType);
        URL presignedUrl = s3Client.generatePresignedUrl(presignedUrlRequest);

        return UserResponseDTO.generatePresignedUrlResponseDTO.builder()
                .presignedUrl(presignedUrl.toString())
                .fileKey(fileKey)
                .build();

    }

    public String generatePresignedUrlForDownload(String objectKey, String originalFilename, long durationSeconds) {
        log.info("Generating download URL for objectKey: {}", objectKey);
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += Duration.ofSeconds(durationSeconds).toMillis(); // 만료 시간 계산
        expiration.setTime(expTimeMillis);

        // --- ▼▼▼ 다운로드를 위한 설정 ▼▼▼ ---
        // 1. Response Header 설정: Content-Disposition을 attachment로 지정하여 다운로드 유도
        ResponseHeaderOverrides responseHeaders = new ResponseHeaderOverrides();
        responseHeaders.setContentDisposition("attachment; filename=\"" + originalFilename + "\"");

        // 2. Presigned URL 생성 요청 객체 생성
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, objectKey)
                        .withMethod(HttpMethod.GET) // 읽기(다운로드)는 GET 메소드 사용
                        .withExpiration(expiration)
                        .withResponseHeaders(responseHeaders); // 다운로드 헤더 설정 적용
        // --- ▲▲▲ ---

        // 3. Presigned URL 생성
        URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);

        return url.toString();
    }
}
