package io.snackdeal.backand.global.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 공통 파일(이미지) 업로드/삭제 API(Swagger) 문서용 합성 어노테이션 모음.
 */
public interface FileApiDocs {

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Tag(name = "공통 - 파일", description = "S3 파일(이미지) 업로드/삭제 (도메인 공통)")
    @interface Doc {
    }

    // 파일 업로드
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "파일 업로드",
            description = "전달받은 파일을 S3 의 지정 디렉토리(directory)에 저장하고 접근 가능한 URL 을 반환\n\n"
                    + "DB 저장은 호출하는 도메인(상품/카테고리 등)에서 별도로 처리해야 함")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "업로드 성공 (S3 URL 반환)"),
            @ApiResponse(responseCode = "500", description = "파일 읽기 실패 또는 S3 업로드 실패")
    })
    @interface Upload {
    }

    // 파일 삭제
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "파일 삭제",
            description = "S3 URL 을 받아 해당 객체를 삭제\n\n"
                    + "수정 시에는 기존 파일을 삭제한 뒤 새 파일을 업로드하는 방식으로 처리")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "400", description = "S3 URL 형식이 아님")
    })
    @interface Delete {
    }
}