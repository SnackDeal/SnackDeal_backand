package io.snackdeal.backand.api.admin.member.controller;

import io.snackdeal.backand.global.config.dto.CommonResponse;
import io.snackdeal.backand.api.user.member.dto.MemberDescription;
import io.snackdeal.backand.api.user.member.dto.MemberDetails;
import io.snackdeal.backand.api.user.member.dto.MemberStatusResponse;
import io.snackdeal.backand.api.user.member.dto.MemberStatusUpdateRequest;
import io.snackdeal.backand.domain.member.entity.MemberStatus;
import io.snackdeal.backand.domain.member.service.MemberService;
import io.snackdeal.backand.global.swagger.AdminMemberApiDocs;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 관리자 회원 관리 API 회원 리스트 조회(검색/필터), 상세 조회, 상태 변경을 담당
 * "/admin/**" 는 SecurityConfig 에서 ROLE_ADMIN 으로 보호됨
 */
@AdminMemberApiDocs.Doc
@RestController
@RequestMapping("/admin/members")
@RequiredArgsConstructor
public class AdminMemberController {

    private final MemberService memberService;

    // 회원 리스트: keyword(이메일/이름) 검색 + status 필터 + 페이징 (모두 선택값)
    @AdminMemberApiDocs.FindAll
    @GetMapping
    public CommonResponse<Page<MemberDescription>> findAll(@RequestParam(required = false) String keyword,
                                                             @RequestParam(required = false) MemberStatus status,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size) {
        return CommonResponse.success(memberService.search(keyword, status, PageRequest.of(page, size)));
    }

    // 회원 상세 조회
    @AdminMemberApiDocs.FindById
    @GetMapping("/{id}")
    public CommonResponse<MemberDescription> findById(@PathVariable Long id) {
        return CommonResponse.success(memberService.findById(id));
    }

    // 회원 상태 변경 본인 계정 변경 차단을 위해 요청 관리자(admin) 정보를 함께 넘긴다.
    @AdminMemberApiDocs.ChangeStatus
    @PatchMapping("/{id}/status")
    public CommonResponse<MemberStatusResponse> changeStatus(@AuthenticationPrincipal MemberDetails admin,
                                                               @PathVariable Long id,
                                                               @Valid @RequestBody MemberStatusUpdateRequest request) {
        return CommonResponse.success(memberService.changeStatus(id, request, admin.getId()));
    }
}
