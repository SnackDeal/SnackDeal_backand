package io.snackdeal.backand.api.admin.cs.controller;

import io.snackdeal.backand.api.admin.cs.dto.AdminQnaAnswerCreateRequest;
import io.snackdeal.backand.api.user.cs.dto.QnaResponse;
import io.snackdeal.backand.domain.cs.service.AdminQnaService;
import io.snackdeal.backand.global.config.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "관리자 - QNA 관리", description = "관리자 1:1 문의 목록 · 상세 조회 · 답변 등록 API (ROLE_ADMIN 필요)")
@RequestMapping("/admin/cs/qna")
@RequiredArgsConstructor
public class AdminQnaController {

    private final AdminQnaService adminQnaService;

    @Operation(summary = "관리자 문의 목록 조회", description = "관리자가 전체 1:1 문의 목록을 최신순으로 조회합니다.")
    @GetMapping
    public CommonResponse<List<QnaResponse>> list() {
        return CommonResponse.success(adminQnaService.findList());
    }

    @Operation(summary = "관리자 문의 상세 조회", description = "관리자가 1:1 문의 상세 내용과 답변 정보를 조회합니다.")
    @GetMapping("/{id}")
    public CommonResponse<QnaResponse> findById(@PathVariable Long id) {
        return CommonResponse.success(adminQnaService.findById(id));
    }

    @Operation(summary = "관리자 문의 답변 등록", description = "관리자가 답변이 없는 1:1 문의에 답변을 등록합니다.")
    @PostMapping("/{id}/answer")
    public CommonResponse<QnaResponse> answer(
            @PathVariable Long id,
            @Valid @RequestBody AdminQnaAnswerCreateRequest request
    ) {
        return CommonResponse.success(adminQnaService.answer(id, request));
    }
}
