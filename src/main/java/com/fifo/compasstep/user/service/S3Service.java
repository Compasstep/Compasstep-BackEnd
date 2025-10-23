package com.fifo.compasstep.user.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.fifo.compasstep.user.dto.UserRequestDTO;
import com.fifo.compasstep.user.dto.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
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
}
