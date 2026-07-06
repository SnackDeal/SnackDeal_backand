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
 * 회원 API(Swagger) 문서용 합성 어노테이션 모음.
 * 컨트롤러에는 상세 설명을 직접 쓰지 않고 여기 정의한 어노테이션(@MemberApiDocs.XXX)만 붙여서 가져다 쓴다.
 * springdoc 은 Spring 의 메타 어노테이션 병합(AnnotatedElementUtils)을 통해 아래 @Operation/@ApiResponses 를 인식한다.
 */
public interface MemberApiDocs {

    // 컨트롤러 클래스에 붙이는 태그(그룹) — Swagger UI 에서 "회원" 섹션으로 묶인다
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Tag(name = "회원", description = "회원가입 · 로그인 · 이메일 인증 · 내 정보 관리 API")
    @interface Doc {
    }

    // 이메일 인증코드 발송 (공개 API → 인증 불필요)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @SecurityRequirements
    @Operation(summary = "이메일 인증코드 발송",
            description = "가입할 이메일로 6자리 인증코드를 발송한다. 유효시간 5분, 60초 후 재발송 가능.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "발송 성공 (data.expiresIn = 유효시간 초)"),
            @ApiResponse(responseCode = "400", description = "이메일 형식 오류"),
            @ApiResponse(responseCode = "409", description = "이미 가입된 이메일"),
            @ApiResponse(responseCode = "429", description = "60초 이내 재요청(재발송 대기 필요)")
    })
    @interface SendCode {
    }

    // 이메일 인증코드 검증 (공개 API)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @SecurityRequirements
    @Operation(summary = "이메일 인증코드 검증",
            description = "발송된 인증코드를 검증하고, 회원가입에 사용할 인증 토큰(verificationToken)을 발급한다. 토큰 유효시간 10분.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검증 성공 (data.verificationToken, data.expiresIn)"),
            @ApiResponse(responseCode = "400", description = "인증코드 불일치 또는 만료")
    })
    @interface VerifyCode {
    }

    // 회원가입 (공개 API)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @SecurityRequirements
    @Operation(summary = "회원가입",
            description = "이메일 인증 토큰(verificationToken)으로 인증을 확인한 뒤 회원을 생성한다.\n\n"
                    + "isSocialLogin=true 인 소셜 회원가입은 이메일 인증 토큰 검증을 생략하고, "
                    + "가입과 동시에 access/refresh 토큰을 발급하여 별도 로그인 없이 세션을 시작한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가입 성공 (data.member = 생성된 회원 정보, 소셜 가입 시 data.accessToken/refreshToken 함께 발급)"),
            @ApiResponse(responseCode = "400", description = "유효성 실패 또는 인증 토큰 무효"),
            @ApiResponse(responseCode = "409", description = "이미 가입된 이메일")
    })
    @interface Join {
    }

    // 로그인 (공개 API)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @SecurityRequirements
    @Operation(summary = "로그인",
            description = "이메일/비밀번호로 로그인하고 access/refresh 토큰을 발급한다. 탈퇴(DELETED) 계정은 로그인할 수 없다.\n\n"
                    + "**샘플 계정**\n"
                    + "- 사용자: `user@snackdeal.io` / `user1234`\n"
                    + "- 관리자: `admin@snackdeal.io` / `admin1234`")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공 (data.accessToken, data.refreshToken)"),
            @ApiResponse(responseCode = "401", description = "존재하지 않는 사용자 / 비밀번호 불일치 / 탈퇴 계정")
    })
    @interface Login {
    }

    // 토큰 재발급 (공개 API — RefreshToken 으로 검증)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @SecurityRequirements
    @Operation(summary = "토큰 재발급",
            description = "RefreshToken 을 검증해 새 access/refresh 토큰을 발급한다(토큰 로테이션).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재발급 성공"),
            @ApiResponse(responseCode = "401", description = "유효하지 않음 / 만료 / 저장된 토큰과 불일치")
    })
    @interface TokenRefresh {
    }

    // 로그아웃 (인증 필요)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "로그아웃",
            description = "저장된 RefreshToken(세션)을 삭제한다. (Bearer 인증 필요)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 접근")
    })
    @interface Logout {
    }

    // 내 정보 조회 (인증 필요)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "내 정보 조회",
            description = "로그인한 사용자 본인의 정보를 조회한다. (Bearer 인증 필요)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 접근")
    })
    @interface GetMe {
    }

    // 내 정보 수정 (인증 필요)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "내 정보 수정",
            description = "휴대폰/비밀번호를 부분 수정한다. 비밀번호 변경 시 현재 비밀번호(currentPassword)가 일치해야 한다. (Bearer 인증 필요)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 실패"),
            @ApiResponse(responseCode = "401", description = "현재 비밀번호 불일치 / 인증되지 않은 접근")
    })
    @interface UpdateMe {
    }
}
