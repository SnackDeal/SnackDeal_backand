package io.snackdeal.backand.api.user.order.controller;

import io.snackdeal.backand.api.admin.order.dto.ShippingPolicyResponse;
import io.snackdeal.backand.domain.order.service.ShippingPolicyService;
import io.snackdeal.backand.global.config.dto.CommonResponse;
import io.snackdeal.backand.global.swagger.ShippingPolicyApiDocs;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 공개 배송비 정책 조회 API. 로그인 없이 누구나 호출 가능(상품상세 등에서 배송비 안내용).
 */
@ShippingPolicyApiDocs.Doc
@RestController
@RequestMapping("/shipping-policy")
@RequiredArgsConstructor
public class ShippingPolicyController {

    private final ShippingPolicyService shippingPolicyService;

    @ShippingPolicyApiDocs.Get
    @GetMapping
    public CommonResponse<ShippingPolicyResponse> get() {
        return CommonResponse.success(shippingPolicyService.get());
    }
}
