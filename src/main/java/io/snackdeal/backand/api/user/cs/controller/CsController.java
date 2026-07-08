package io.snackdeal.backand.api.user.cs.controller;

import io.snackdeal.backand.api.user.cs.dto.FaqResponse;
import io.snackdeal.backand.domain.cs.entity.QnaType;
import io.snackdeal.backand.domain.cs.service.CsService;
import io.snackdeal.backand.global.config.dto.CommonResponse;
import io.snackdeal.backand.api.user.member.dto.MemberDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CsController {

    private final CsService csService;

    @GetMapping("/cs/notice/list")
    public CommonResponse<Object> noticeList() {
        return CommonResponse.success(csService.findNoticeList());
    }

    @GetMapping("/cs/notice/{id}")
    public CommonResponse<Object> noticeDetail(@PathVariable Long id) {
        return CommonResponse.success(csService.findNoticeById(id));
    }

    @GetMapping("/cs/qna/faq")
    public CommonResponse<List<FaqResponse>> faqList(@RequestParam(required = false) QnaType type) {
        return CommonResponse.success(csService.findFaqList(type));
    }

    @GetMapping("/cs/qna/list")
    public CommonResponse<Object> myQnaList(@AuthenticationPrincipal MemberDetails details) {
        return CommonResponse.success(csService.findMyQnaList(details.getEmail()));
    }

    @PostMapping("/cs/qna")
    public CommonResponse<Object> createQna(@AuthenticationPrincipal MemberDetails details, @RequestBody Object request) {
        return CommonResponse.success(csService.createQna(details.getEmail(), request));
    }

    @GetMapping("/cs/qna/{id}")
    public CommonResponse<Object> qnaDetail(@AuthenticationPrincipal MemberDetails details, @PathVariable Long id) {
        return CommonResponse.success(csService.findQnaById(details.getEmail(), id));
    }

    @PatchMapping("/cs/qna/{id}")
    public CommonResponse<Object> updateQna(@AuthenticationPrincipal MemberDetails details,
                                             @PathVariable Long id, @RequestBody Object request) {
        return CommonResponse.success(csService.updateQna(details.getEmail(), id, request));
    }

    @DeleteMapping("/cs/qna/{id}")
    public CommonResponse<Void> deleteQna(@AuthenticationPrincipal MemberDetails details, @PathVariable Long id) {
        csService.deleteQna(details.getEmail(), id);
        return CommonResponse.success(null);
    }

    @PostMapping("/chatbot/ask")
    public CommonResponse<Object> askChatbot(@RequestBody Object request) {
        return CommonResponse.success(csService.askChatbot(request));
    }
}
