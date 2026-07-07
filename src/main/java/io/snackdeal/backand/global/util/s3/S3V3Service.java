package io.snackdeal.backand.global.util.s3;


import org.springframework.web.multipart.MultipartFile;

public interface S3V3Service {

    // S3 업로드 (DB 저장은 호출하는 쪽에서 처리)
    String upload(MultipartFile file, String directory);

    // S3 파일 삭제
    void delete(String url);

}
