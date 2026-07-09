package io.snackdeal.backand.api.admin.cs.controller;

import io.snackdeal.backand.api.admin.cs.dto.AdminNoticeCreateRequest;
import io.snackdeal.backand.api.admin.cs.dto.AdminNoticeUpdateRequest;
import io.snackdeal.backand.api.user.cs.dto.NoticeResponse;
import io.snackdeal.backand.api.user.cs.dto.NoticeSummaryResponse;
import io.snackdeal.backand.domain.cs.service.AdminNoticeService;
import io.snackdeal.backand.global.config.dto.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/cs/notice")
@RequiredArgsConstructor
public class AdminNoticeController {

    private final AdminNoticeService adminNoticeService;

    @GetMapping
    public CommonResponse<List<NoticeSummaryResponse>> list() {
        return CommonResponse.success(adminNoticeService.findList());
    }

    @GetMapping("/{id}")
    public CommonResponse<NoticeResponse> findById(@PathVariable Long id) {
        return CommonResponse.success(adminNoticeService.findById(id));
    }

    @PostMapping
    public CommonResponse<NoticeResponse> create(@Valid @RequestBody AdminNoticeCreateRequest request) {
        return CommonResponse.success(adminNoticeService.create(request));
    }

    @PutMapping("/{id}")
    public CommonResponse<NoticeResponse> update(@PathVariable Long id, @Valid @RequestBody AdminNoticeUpdateRequest request) {
        return CommonResponse.success(adminNoticeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public CommonResponse<Void> delete(@PathVariable Long id) {
        adminNoticeService.delete(id);
        return CommonResponse.success(null);
    }
}