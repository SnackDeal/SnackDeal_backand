package io.snackdeal.backand.api.admin.member.controller;

import io.snackdeal.backand.global.config.dto.CommonResponse;
import io.snackdeal.backand.api.user.member.dto.MemberDescription;
import io.snackdeal.backand.api.user.member.dto.MemberStatusUpdateRequest;
import io.snackdeal.backand.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/members")
@RequiredArgsConstructor
public class AdminMemberController {

    private final MemberService memberService;

    @GetMapping
    public CommonResponse<Page<MemberDescription>> findAll(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size) {
        return CommonResponse.success(memberService.findAll(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public CommonResponse<MemberDescription> findById(@PathVariable Long id) {
        return CommonResponse.success(memberService.findById(id));
    }

    @PatchMapping("/{id}/status")
    public CommonResponse<MemberDescription> changeStatus(@PathVariable Long id,
                                                            @Valid @RequestBody MemberStatusUpdateRequest request) {
        return CommonResponse.success(memberService.changeStatus(id, request));
    }
}
