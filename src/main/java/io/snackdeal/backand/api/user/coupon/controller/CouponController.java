package io.snackdeal.backand.api.user.coupon.controller;

import io.snackdeal.backand.api.user.coupon.dto.CouponDownloadResponse;
import io.snackdeal.backand.api.user.coupon.dto.EventCouponBoardListResponse;
import io.snackdeal.backand.api.user.coupon.dto.EventCouponDetailResponse;
import io.snackdeal.backand.api.user.coupon.dto.MyCouponListResponse;
import io.snackdeal.backand.domain.coupon.service.CouponService;
import io.snackdeal.backand.global.config.dto.CommonResponse;
import io.snackdeal.backand.api.user.member.dto.MemberDetails;
import io.snackdeal.backand.domain.coupon.entity.UserCouponStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @GetMapping("/event/coupon/list")
    public CommonResponse<EventCouponBoardListResponse> eventCouponList() {
        return CommonResponse.success(couponService.findEventCouponBoards());
    }

    @GetMapping("/event/coupon-board/{boardId}")
    public CommonResponse<EventCouponDetailResponse> eventCouponDetail(
            @PathVariable Long boardId,
            @AuthenticationPrincipal MemberDetails details
    ) {
        Long memberId = details == null ? null : details.getId();
        return CommonResponse.success(couponService.findEventCouponDetail(memberId, boardId));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/event/coupon/{couponId}/download")
    public CommonResponse<CouponDownloadResponse> download(
            @AuthenticationPrincipal MemberDetails details,
            @PathVariable Long couponId
    ) {
        return CommonResponse.created(couponService.download(details.getId(), couponId));
    }

    @GetMapping("/mypage/coupon")
    public CommonResponse<MyCouponListResponse> myCoupons(
            @AuthenticationPrincipal MemberDetails details,
            @RequestParam(required = false) UserCouponStatus status
    ) {
        return CommonResponse.success(couponService.findMyCoupons(details.getId(), status));
    }
}
