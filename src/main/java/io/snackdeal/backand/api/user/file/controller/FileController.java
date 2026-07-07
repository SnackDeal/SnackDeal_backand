package io.snackdeal.backand.api.user.file.controller;

import io.snackdeal.backand.api.user.file.dto.FileUploadResponse;
import io.snackdeal.backand.global.config.dto.CommonResponse;
import io.snackdeal.backand.global.swagger.FileApiDocs;
import io.snackdeal.backand.global.util.s3.S3V3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Swagger 설명은 global 의 @FileApiDocs 에서 가져온다.
 */
@FileApiDocs.Doc
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final S3V3Service s3V3Service;

    // 도메인 상관없이 파일 업로드 후 URL 반환 — DB 저장은 각 도메인에서 처리
    @FileApiDocs.Upload
    @PostMapping
    public CommonResponse<FileUploadResponse> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("directory") String directory
    ) {
        String url = s3V3Service.upload(file, directory);
        return CommonResponse.success(new FileUploadResponse(url));
    }

    // 수정시 삭제 후 수정해야한다고함


    @FileApiDocs.Delete
    @DeleteMapping
    public CommonResponse<Void> delete(@RequestParam("url") String url) {
        s3V3Service.delete(url);
        return CommonResponse.success(null);
    }
}
