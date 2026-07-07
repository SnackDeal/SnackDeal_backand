package io.snackdeal.backand.global.util.s3.impl;

import io.snackdeal.backand.global.util.s3.S3V3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
public class S3V3ServiceImpl implements S3V3Service {

    // final 없이 필드 선언
    private S3Client s3Client;
    private String bucketName;
    private Environment env;

    // 생성자
    public S3V3ServiceImpl(Environment env, S3Client s3Client) {
        this.s3Client = s3Client;
        this.bucketName = env.getProperty("custom.cloud.s3.bucket");
        this.env = env;
    }

    // putObject() 구조 그대로 — 도메인 종속 제거, 업로드 후 URL만 반환
    @Override
    public String upload(MultipartFile file, String directory) {
        try {
            String fileName = file.getOriginalFilename();
            String contentType = file.getContentType();
            String path = directory + "/";
            String key = path + UUID.randomUUID() + "_" + fileName;

            //  PutObjectRequest 빌더
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .contentLength(file.getSize())
                    .build();

            PutObjectResponse response = s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromBytes(file.getBytes())
            );

            if (response.sdkHttpResponse().statusText().orElse("FAIL").equals("OK")) {
                return "https://" + bucketName + ".s3.ap-northeast-2.amazonaws.com/" + key;
            } else {
                throw new IllegalStateException("AWS에 파일을 올리는데 실패했습니다.");
            }

        } catch (IOException ie) {
            log.error("파일을 읽어들이는데 에러가 발생했습니다.");
            log.error(ie.getMessage());
            throw new RuntimeException(ie.getMessage());
        } catch (S3Exception ae) {
            log.error("AWS와 통신에 문제가 발생했습니다.");
            log.error(ae.getMessage());
            throw new RuntimeException(ae.getMessage());
        } catch (IllegalStateException se) {
            log.error("AWS에 파일을 올리는데 실패했습니다.");
            log.error(se.getMessage());
            throw new RuntimeException(se.getMessage());
        }
    }

    // S3 파일 삭제
    @Override
    public void delete(String url) {
        String key = extractKey(url);
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3Client.deleteObject(request);
    }

    // URL에서 S3 key 추출
    private String extractKey(String url) {
        String prefix = "https://" + bucketName + ".s3.ap-northeast-2.amazonaws.com/";
        if (url.startsWith(prefix)) {
            return url.substring(prefix.length());
        }
        throw new IllegalArgumentException("S3 URL 형식이 아닙니다: " + url);
    }
}
