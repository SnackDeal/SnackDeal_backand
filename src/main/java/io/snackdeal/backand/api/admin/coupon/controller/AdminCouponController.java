package io.snackdeal.backand.api.admin.coupon.controller;

import io.snackdeal.backand.domain.coupon.service.AdminCouponService;
import io.snackdeal.backand.global.config.dto.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AdminCouponController {

    private final AdminCouponService adminCouponService;

    @GetMapping("/admin/coupon")
    public CommonResponse<Object> list() {
        return CommonResponse.success(adminCouponService.findList());
    }

    @PostMapping("/admin/coupon")
    public CommonResponse<Object> save(@RequestBody Object request) {
        return CommonResponse.success(adminCouponService.save(request));
    }

    @PutMapping("/admin/coupon/{id}")
    public CommonResponse<Object> update(@PathVariable Long id, @RequestBody Object request) {
        return CommonResponse.success(adminCouponService.update(id, request));
    }

    @PatchMapping("/admin/coupon/{id}/status")
    public CommonResponse<Object> changeStatus(@PathVariable Long id, @RequestBody Object request) {
        return CommonResponse.success(adminCouponService.changeStatus(id, request));
    }

    @GetMapping("/admin/coupon-board")
    public CommonResponse<Object> boardList() {
        return CommonResponse.success(adminCouponService.findBoardList());
    }

    @PostMapping("/admin/coupon-board")
    public CommonResponse<Object> saveBoard(@RequestBody Object request) {
        return CommonResponse.success(adminCouponService.saveBoard(request));
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
