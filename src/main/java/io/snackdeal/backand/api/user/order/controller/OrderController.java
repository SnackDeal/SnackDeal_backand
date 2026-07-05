package io.snackdeal.backand.api.user.order.controller;

import io.snackdeal.backand.api.user.member.dto.MemberDetails;
import io.snackdeal.backand.api.user.order.dto.OrderCompleteRequest;
import io.snackdeal.backand.api.user.order.dto.OrderCompleteResponse;
import io.snackdeal.backand.api.user.order.dto.OrderListResponse;
import io.snackdeal.backand.api.user.order.dto.OrderPrepareRequest;
import io.snackdeal.backand.api.user.order.dto.OrderPrepareResponse;
import io.snackdeal.backand.api.user.order.dto.OrderResponse;
import io.snackdeal.backand.api.user.order.dto.RefundRequest;
import io.snackdeal.backand.api.user.order.dto.RefundResponse;
import io.snackdeal.backand.domain.order.service.OrderService;
import io.snackdeal.backand.global.config.dto.CommonResponse;
import io.snackdeal.backand.global.swagger.OrderApiDocs;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 주문/결제 API.
 * 결제 흐름: prepare(주문 임시생성·금액확정) → 프론트 포트원 결제 → complete(서버 검증·확정).
 * 이후 주문내역 조회/상세/환불요청을 제공한다. "/order/**" 는 SecurityConfig 에서 인증 필요.
 */
@OrderApiDocs.Doc
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @OrderApiDocs.Prepare
    @PostMapping("/prepare")
    public CommonResponse<OrderPrepareResponse> prepare(@AuthenticationPrincipal MemberDetails details,
                                                        @Valid @RequestBody OrderPrepareRequest request) {
        return CommonResponse.success(orderService.prepare(details.getEmail(), request));
    }

    @OrderApiDocs.Complete
    @PostMapping("/complete")
    public CommonResponse<OrderCompleteResponse> complete(@AuthenticationPrincipal MemberDetails details,
                                                          @Valid @RequestBody OrderCompleteRequest request) {
        return CommonResponse.success(orderService.complete(details.getEmail(), request));
    }

    @OrderApiDocs.List
    @GetMapping("/list")
    public CommonResponse<OrderListResponse> list(@AuthenticationPrincipal MemberDetails details,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        return CommonResponse.success(orderService.findList(details.getEmail(), page, size));
    }

    @OrderApiDocs.FindById
    @GetMapping("/{orderId}")
    public CommonResponse<OrderResponse> findById(@AuthenticationPrincipal MemberDetails details,
                                                  @PathVariable Long orderId) {
        return CommonResponse.success(orderService.findById(details.getEmail(), orderId));
    }

    @OrderApiDocs.Refund
    @PostMapping("/{orderId}/refund")
    public CommonResponse<RefundResponse> refund(@AuthenticationPrincipal MemberDetails details,
                                                 @PathVariable Long orderId,
                                                 @Valid @RequestBody RefundRequest request) {
        return CommonResponse.success(orderService.refund(details.getEmail(), orderId, request));
    }
}
