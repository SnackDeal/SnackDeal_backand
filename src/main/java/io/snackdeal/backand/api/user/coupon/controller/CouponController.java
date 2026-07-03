package io.snackdeal.backand.api.user.coupon.controller;

import io.snackdeal.backand.domain.coupon.service.CouponService;
import io.snackdeal.backand.global.config.dto.CommonResponse;
import io.snackdeal.backand.api.user.member.dto.MemberDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @GetMapping("/event/coupon/list")
    public CommonResponse<Object> eventCouponList() {
        return CommonResponse.success(couponService.findEventCouponList());
    }

    @PostMapping("/event/coupon/{couponId}/download")
    public CommonResponse<Object> download(@AuthenticationPrincipal MemberDetails details, @PathVariable Long couponId) {
        return CommonResponse.success(couponService.download(details.getEmail(), couponId));
    }

    @GetMapping("/mypage/coupon")
    public CommonResponse<Object> myCoupons(@AuthenticationPrincipal MemberDetails details) {
        return CommonResponse.success(couponService.findMyCoupons(details.getEmail()));
    }
}
