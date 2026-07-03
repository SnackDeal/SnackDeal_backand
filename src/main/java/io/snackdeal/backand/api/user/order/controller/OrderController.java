package io.snackdeal.backand.api.user.order.controller;

import io.snackdeal.backand.global.config.dto.CommonResponse;
import io.snackdeal.backand.api.user.member.dto.MemberDetails;
import io.snackdeal.backand.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/prepare")
    public CommonResponse<Object> prepare(@AuthenticationPrincipal MemberDetails details, @RequestBody Object request) {
        return CommonResponse.success(orderService.prepare(details.getEmail(), request));
    }

    @PostMapping("/complete")
    public CommonResponse<Object> complete(@AuthenticationPrincipal MemberDetails details, @RequestBody Object request) {
        return CommonResponse.success(orderService.complete(details.getEmail(), request));
    }

    @GetMapping("/list")
    public CommonResponse<Object> list(@AuthenticationPrincipal MemberDetails details) {
        return CommonResponse.success(orderService.findList(details.getEmail()));
    }

    @GetMapping("/{orderId}")
    public CommonResponse<Object> findById(@AuthenticationPrincipal MemberDetails details, @PathVariable Long orderId) {
        return CommonResponse.success(orderService.findById(details.getEmail(), orderId));
    }

    @PostMapping("/{orderId}/refund")
    public CommonResponse<Object> refund(@AuthenticationPrincipal MemberDetails details, @PathVariable Long orderId) {
        return CommonResponse.success(orderService.refund(details.getEmail(), orderId));
    }
}
