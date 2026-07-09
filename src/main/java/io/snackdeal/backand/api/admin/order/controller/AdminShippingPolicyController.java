package io.snackdeal.backand.api.admin.order.controller;

import io.snackdeal.backand.api.admin.order.dto.ShippingPolicyResponse;
import io.snackdeal.backand.api.admin.order.dto.ShippingPolicyUpdateRequest;
import io.snackdeal.backand.domain.order.service.ShippingPolicyService;
import io.snackdeal.backand.global.config.dto.CommonResponse;
import io.snackdeal.backand.global.swagger.AdminOrderApiDocs;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 배송비 정책 관리 API(관리자) 무료기준/기본 배송비를 조회·변경
 * "/admin/**" 는 SecurityConfig 에서 ROLE_ADMIN 으로 보호됨
 */
@AdminOrderApiDocs.PolicyDoc
@RestController
@RequestMapping("/admin/shipping-policy")
@RequiredArgsConstructor
public class AdminShippingPolicyController {

    private final ShippingPolicyService shippingPolicyService;

    @AdminOrderApiDocs.ShippingPolicyGet
    @GetMapping
    public CommonResponse<ShippingPolicyResponse> get() {
        return CommonResponse.success(shippingPolicyService.get());
    }

    @AdminOrderApiDocs.ShippingPolicyUpdate
    @PatchMapping
    public CommonResponse<ShippingPolicyResponse> update(@Valid @RequestBody ShippingPolicyUpdateRequest request) {
        return CommonResponse.success(shippingPolicyService.update(request));
    }
}
