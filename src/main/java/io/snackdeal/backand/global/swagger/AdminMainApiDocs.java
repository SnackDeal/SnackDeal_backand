package io.snackdeal.backand.global.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 관리자 로그인/메인(대시보드) API(Swagger) 문서용 합성 어노테이션 모음.
 */
public interface AdminMainApiDocs {

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Tag(name = "관리자 - 로그인/대시보드", description = "관리자 로그인 · 대시보드 요약 지표")
    @interface Doc {
    }

    // 관리자 로그인 (공개 API — 로그인 자체는 인증 전)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @SecurityRequirements
    @Operation(summary = "관리자 로그인",
            description = "이메일/비밀번호로 로그인하고, 역할이 ADMIN 인 경우에만 토큰을 발급한다.\n\n"
                    + "**샘플 관리자 계정**: `admin@snackdeal.io` / `admin1234`")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공 (access/refresh 토큰)"),
            @ApiResponse(responseCode = "401", description = "존재하지 않는 사용자 / 비밀번호 불일치"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
    })
    @interface AdminLogin {
    }

    // 관리자 로그아웃
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "관리자 로그아웃", description = "Redis 의 RefreshToken/세션을 삭제해 로그인 세션을 무효화한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @interface AdminLogout {
    }

    // 대시보드 요약 (관리자 인증 필요)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "대시보드 요약 지표",
            description = "오늘 주문 수/매출, 신규 회원 수, 저재고 상품 수, 미답변 QnA 수를 집계해 반환한다. (관리자 전용)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
    })
    @interface Dashboard {
    }
}
