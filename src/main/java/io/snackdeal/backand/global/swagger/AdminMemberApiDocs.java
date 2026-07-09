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
 * 관리자 회원 관리 API(Swagger) 문서용 합성 어노테이션 모음.
 * 모든 엔드포인트는 ROLE_ADMIN(Bearer) 이 필요하다 (전역 SecurityScheme 적용)
 */
public interface AdminMemberApiDocs {

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Tag(name = "관리자 - 회원 관리", description = "회원 리스트/상세 조회 · 상태 변경 (관리자 전용)")
    @interface Doc {
    }

    // 회원 리스트 (검색/필터/페이징)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "회원 리스트 조회",
            description = "keyword(이메일/이름 부분검색)와 status 필터로 회원을 조회 페이지네이션(page, size) 지원.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공 (Page<회원>)"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
    })
    @interface FindAll {
    }

    // 회원 상세
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "회원 상세 조회",
            description = "회원 id 로 기본 정보를 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원")
    })
    @interface FindById {
    }

    // 회원 상태 변경
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "회원 상태 변경",
            description = "회원 상태를 ACTIVE/INACTIVE/DELETED 로 변경 하드 삭제하지 않고 상태+deleted_at 으로 처리 "
                    + "본인 계정은 변경 불가(403), 이미 탈퇴한 회원은 되돌릴 수 없다(422) DELETED 전환 시 세션(토큰)이 즉시 무효화됨")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상태 변경 성공"),
            @ApiResponse(responseCode = "400", description = "허용되지 않는 상태값"),
            @ApiResponse(responseCode = "403", description = "본인 계정 변경 차단 / 관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원"),
            @ApiResponse(responseCode = "422", description = "잘못된 상태 전이(탈퇴 회원)")
    })
    @interface ChangeStatus {
    }
}
