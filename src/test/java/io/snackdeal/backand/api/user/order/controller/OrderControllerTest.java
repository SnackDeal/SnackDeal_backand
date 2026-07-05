package io.snackdeal.backand.api.user.order.controller;

import io.snackdeal.backand.api.user.member.dto.MemberDetails;
import io.snackdeal.backand.api.user.order.dto.OrderCompleteRequest;
import io.snackdeal.backand.api.user.order.dto.OrderItemRequest;
import io.snackdeal.backand.api.user.order.dto.OrderListResponse;
import io.snackdeal.backand.api.user.order.dto.OrderPrepareRequest;
import io.snackdeal.backand.api.user.order.dto.OrderPrepareResponse;
import io.snackdeal.backand.api.user.order.dto.OrderResponse;
import io.snackdeal.backand.api.user.order.dto.RefundRequest;
import io.snackdeal.backand.api.user.order.dto.RefundResponse;
import io.snackdeal.backand.api.user.order.dto.ShippingRequest;
import io.snackdeal.backand.domain.member.entity.MemberRole;
import io.snackdeal.backand.domain.order.entity.OrderStatus;
import io.snackdeal.backand.domain.order.service.OrderService;
import io.snackdeal.backand.global.config.dto.CommonResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private OrderService orderService;

    private final MemberDetails details = new MemberDetails(1L, "u@test.com", "ENCODED", MemberRole.USER);

    @Test
    @DisplayName("prepare - 로그인 이메일과 요청을 서비스에 위임한다")
    void prepare() {
        OrderPrepareRequest request = new OrderPrepareRequest(
                List.of(new OrderItemRequest(1L, 2)), null,
                new ShippingRequest("홍길동", "01012345678", "06133", "서울", null, null), null);
        OrderPrepareResponse expected = new OrderPrepareResponse("ORD-1", 12000L, "u@test.com", "홍길동", "01011112222");
        when(orderService.prepare(eq("u@test.com"), eq(request))).thenReturn(expected);

        CommonResponse<OrderPrepareResponse> response = orderController.prepare(details, request);

        assertSame(expected, response.getData());
        verify(orderService).prepare("u@test.com", request);
    }

    @Test
    @DisplayName("complete - 결제 검증 요청을 위임한다")
    void complete() {
        OrderCompleteRequest request = new OrderCompleteRequest("imp1", "ORD-1");
        when(orderService.complete(eq("u@test.com"), eq(request))).thenReturn(null);

        orderController.complete(details, request);

        verify(orderService).complete("u@test.com", request);
    }

    @Test
    @DisplayName("list - page/size 를 서비스에 전달한다")
    void list() {
        OrderListResponse expected = new OrderListResponse(List.of(), 0, 10, 0);
        when(orderService.findList("u@test.com", 0, 10)).thenReturn(expected);

        CommonResponse<OrderListResponse> response = orderController.list(details, 0, 10);

        assertSame(expected, response.getData());
    }

    @Test
    @DisplayName("findById - 주문 상세 조회를 위임한다")
    void findById() {
        OrderResponse expected = new OrderResponse(1L, "ORD-1", null, OrderStatus.PAYMENT_COMPLETED,
                List.of(), null, null);
        when(orderService.findById("u@test.com", 1L)).thenReturn(expected);

        CommonResponse<OrderResponse> response = orderController.findById(details, 1L);

        assertSame(expected, response.getData());
    }

    @Test
    @DisplayName("refund - 환불 요청을 위임한다")
    void refund() {
        RefundRequest request = new RefundRequest("단순 변심");
        RefundResponse expected = new RefundResponse(1L, "ORD-1", OrderStatus.REFUND_REQUESTED);
        when(orderService.refund("u@test.com", 1L, request)).thenReturn(expected);

        CommonResponse<RefundResponse> response = orderController.refund(details, 1L, request);

        assertSame(expected, response.getData());
        verify(orderService).refund("u@test.com", 1L, request);
    }
}
