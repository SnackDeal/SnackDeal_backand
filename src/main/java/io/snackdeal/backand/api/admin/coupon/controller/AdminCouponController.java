package io.snackdeal.backand.api.admin.coupon.controller;

import io.snackdeal.backand.api.admin.coupon.dto.AdminCouponBoardCreateRequest;
import io.snackdeal.backand.api.admin.coupon.dto.AdminCouponBoardListResponse;
import io.snackdeal.backand.api.admin.coupon.dto.AdminCouponBoardResponse;
import io.snackdeal.backand.api.admin.coupon.dto.AdminCouponCreateRequest;
import io.snackdeal.backand.api.admin.coupon.dto.AdminCouponCreateResponse;
import io.snackdeal.backand.api.admin.coupon.dto.AdminCouponListResponse;
import io.snackdeal.backand.api.admin.coupon.dto.AdminCouponStatusResponse;
import io.snackdeal.backand.api.admin.coupon.dto.AdminCouponStatusUpdateRequest;
import io.snackdeal.backand.domain.coupon.entity.CouponStatus;
import io.snackdeal.backand.domain.coupon.entity.IssueType;
import io.snackdeal.backand.domain.coupon.service.AdminCouponService;
import io.snackdeal.backand.global.config.dto.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AdminCouponController {

    private final AdminCouponService adminCouponService;

    @GetMapping("/admin/coupon")
    public CommonResponse<AdminCouponListResponse> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) IssueType issueType,
            @RequestParam(required = false) CouponStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return CommonResponse.success(adminCouponService.findList(keyword, issueType, status, page, size));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/admin/coupon")
    public CommonResponse<AdminCouponCreateResponse> save(@Valid @RequestBody AdminCouponCreateRequest request) {
        return CommonResponse.created(adminCouponService.save(request));
    }

    @PutMapping("/admin/coupon/{id}")
    public CommonResponse<Object> update(@PathVariable Long id, @RequestBody Object request) {
        return CommonResponse.success(adminCouponService.update(id, request));
    }

    @PatchMapping("/admin/coupon/{id}/status")
    public CommonResponse<AdminCouponStatusResponse> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody AdminCouponStatusUpdateRequest request
    ) {
        return CommonResponse.success(adminCouponService.changeStatus(id, request));
    }

    @GetMapping("/admin/coupon-board")
    public CommonResponse<AdminCouponBoardListResponse> boardList() {
        return CommonResponse.success(adminCouponService.findBoardList());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/admin/coupon-board")
    public CommonResponse<AdminCouponBoardResponse> saveBoard(
            @Valid @RequestBody AdminCouponBoardCreateRequest request
    ) {
        return CommonResponse.created(adminCouponService.saveBoard(request));
    }

    @PutMapping("/admin/coupon-board/{id}")
    public CommonResponse<Object> updateBoard(@PathVariable Long id, @RequestBody Object request) {
        return CommonResponse.success(adminCouponService.updateBoard(id, request));
    }

    @DeleteMapping("/admin/coupon-board/{id}")
    public CommonResponse<Void> deleteBoard(@PathVariable Long id) {
        adminCouponService.deleteBoard(id);
        return CommonResponse.success(null);
    }
}
