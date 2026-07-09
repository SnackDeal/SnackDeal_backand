package io.snackdeal.backand.api.user.cs.controller;

import io.snackdeal.backand.api.user.cs.dto.FaqResponse;
import io.snackdeal.backand.domain.cs.entity.QnaType;
import io.snackdeal.backand.api.user.cs.dto.*;
import io.snackdeal.backand.api.user.member.dto.MemberDetails;
import io.snackdeal.backand.domain.cs.service.CsService;
import io.snackdeal.backand.global.config.dto.CommonResponse;
import io.snackdeal.backand.global.config.code.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "고객센터", description = "공지사항 · FAQ · 1:1 문의 · 챗봇 API")
@RequiredArgsConstructor
public class CsController {

    private final CsService csService;

    @Operation(summary = "공지사항 목록 조회", description = "고객센터 공지사항 목록을 조회합니다.")
    @GetMapping("/cs/notice/list")
    public CommonResponse<Object> noticeList() {
        return CommonResponse.success(csService.findNoticeList());
    }

    @Operation(summary = "공지사항 상세 조회", description = "공지사항 ID로 고객센터 공지사항 상세 내용을 조회합니다.")
    @GetMapping("/cs/notice/{id}")
    public CommonResponse<Object> noticeDetail(@PathVariable Long id) {
        return CommonResponse.success(csService.findNoticeById(id));
    }

    @Operation(summary = "FAQ 목록 조회", description = "고객센터 FAQ 목록을 조회합니다. 공개 API입니다.")
    @GetMapping("/cs/qna/faq")
    public CommonResponse<List<FaqResponse>> faqList(@RequestParam(required = false) QnaType type) {
        return CommonResponse.success(csService.findFaqList(type));
    }

    @Operation(summary = "내 문의 목록 조회", description = "로그인 사용자의 1:1 문의 목록을 최신순으로 조회합니다.")
    @GetMapping("/cs/qna/list")
    public CommonResponse<List<QnaSummaryResponse>> myQnaList(
            @AuthenticationPrincipal MemberDetails details
    ) {
        return CommonResponse.success(csService.findMyQnaList(details.getId()));
    }

    @Operation(summary = "문의 등록", description = "로그인 사용자가 1:1 문의를 등록합니다.")
    @PostMapping("/cs/qna")
    public CommonResponse<QnaResponse> createQna(
            @AuthenticationPrincipal MemberDetails details,
            @Valid @RequestBody QnaCreateRequest request
    ) {
        return CommonResponse.created(csService.createQna(details.getId(), request));
    }

    @Operation(summary = "문의 상세 조회", description = "로그인 사용자가 본인 1:1 문의 상세 내용을 조회합니다.")
    @GetMapping("/cs/qna/{id}")
    public CommonResponse<QnaResponse> qnaDetail(
            @AuthenticationPrincipal MemberDetails details,
            @PathVariable Long id
    ) {
        return CommonResponse.success(csService.findQnaById(details.getId(), id));
    }

    @Operation(summary = "문의 수정", description = "답변 전 상태의 본인 1:1 문의를 수정합니다.")
    @PatchMapping("/cs/qna/{id}")
    public CommonResponse<QnaResponse> updateQna(
            @AuthenticationPrincipal MemberDetails details,
            @PathVariable Long id,
            @Valid @RequestBody QnaUpdateRequest request
    ) {
        return CommonResponse.success(csService.updateQna(details.getId(), id, request));
    }

    @Operation(summary = "문의 삭제", description = "답변 전 상태의 본인 1:1 문의를 삭제합니다.")
    @DeleteMapping("/cs/qna/{id}")
    public CommonResponse<Void> deleteQna(
            @AuthenticationPrincipal MemberDetails details,
            @PathVariable Long id
    ) {
        csService.deleteQna(details.getId(), id);
        return CommonResponse.success(null);
    }

    @Operation(summary = "챗봇 질문", description = "고객센터 챗봇에 질문을 전달합니다.")
    @PostMapping("/chatbot/ask")
    public CommonResponse<Object> askChatbot(@RequestBody Object request) {
        return CommonResponse.success(csService.askChatbot(request));
    }
}
