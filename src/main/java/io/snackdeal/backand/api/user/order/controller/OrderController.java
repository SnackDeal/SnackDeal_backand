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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 주문/결제 API.
 * 결제 흐름: prepare(주문 임시생성·금액확정) → 프론트 포트원 결제 → complete(서버 검증·확정).
 * 이후 주문내역 조회/상세/환불요청을 제공한다. "/order/**" 는 SecurityConfig 에서 인증 필요.
 */
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 주문 준비: 재고 체크 후 주문을 PENDING 으로 만들고 결제 예정 금액/구매자 정보를 반환한다.
    @PostMapping("/prepare")
    public CommonResponse<OrderPrepareResponse> prepare(@AuthenticationPrincipal MemberDetails details,
                                                        @Valid @RequestBody OrderPrepareRequest request) {
        return CommonResponse.success(orderService.prepare(details.getEmail(), request));
    }

    // 결제 검증 및 주문 확정: 포트원 실제 결제금액과 대조 후 재고 차감·쿠폰 사용·확정 처리.
    @PostMapping("/complete")
    public CommonResponse<OrderCompleteResponse> complete(@AuthenticationPrincipal MemberDetails details,
                                                          @Valid @RequestBody OrderCompleteRequest request) {
        return CommonResponse.success(orderService.complete(details.getEmail(), request));
    }

    // 내 주문내역 (최신순 페이징).
    @GetMapping("/list")
    public CommonResponse<OrderListResponse> list(@AuthenticationPrincipal MemberDetails details,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        return CommonResponse.success(orderService.findList(details.getEmail(), page, size));
    }

    // 주문 상세 (본인 주문만).
    @GetMapping("/{orderId}")
    public CommonResponse<OrderResponse> findById(@AuthenticationPrincipal MemberDetails details,
                                                  @PathVariable Long orderId) {
        return CommonResponse.success(orderService.findById(details.getEmail(), orderId));
    }

    // 환불 요청 (결제완료/배송준비중에서만 가능).
    @PostMapping("/{orderId}/refund")
    public CommonResponse<RefundResponse> refund(@AuthenticationPrincipal MemberDetails details,
                                                 @PathVariable Long orderId,
                                                 @Valid @RequestBody RefundRequest request) {
        return CommonResponse.success(orderService.refund(details.getEmail(), orderId, request));
    }
}
