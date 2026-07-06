package io.snackdeal.backand.api.admin.order.controller;

import io.snackdeal.backand.api.admin.order.dto.AdminOrderDetailResponse;
import io.snackdeal.backand.api.admin.order.dto.AdminOrderListResponse;
import io.snackdeal.backand.api.admin.order.dto.AdminOrderStatusRequest;
import io.snackdeal.backand.api.admin.order.dto.AdminOrderStatusResponse;
import io.snackdeal.backand.api.admin.order.dto.AdminRefundRequest;
import io.snackdeal.backand.api.admin.order.dto.AdminRefundResponse;
import io.snackdeal.backand.domain.order.entity.OrderStatus;
import io.snackdeal.backand.domain.order.service.AdminOrderService;
import io.snackdeal.backand.global.config.dto.CommonResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminOrderControllerTest {

    @InjectMocks
    private AdminOrderController adminOrderController;

    @Mock
    private AdminOrderService adminOrderService;

    @Test
    @DisplayName("list - 필터/기간을 서비스에 전달하고, 날짜는 시간 범위로 변환한다")
    void list() {
        AdminOrderListResponse expected = new AdminOrderListResponse(List.of(), 0, 20, 0);
        when(adminOrderService.findList(eq("hong"), eq(OrderStatus.PREPARING_SHIPMENT), any(), any(), eq(0), eq(20)))
                .thenReturn(expected);

        CommonResponse<AdminOrderListResponse> response = adminOrderController.list(
                "hong", OrderStatus.PREPARING_SHIPMENT, LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 5), 0, 20);

        assertSame(expected, response.getData());
        verify(adminOrderService).findList(eq("hong"), eq(OrderStatus.PREPARING_SHIPMENT), any(), any(), eq(0), eq(20));
    }

    @Test
    @DisplayName("findById - 주문 상세를 위임한다")
    void findById() {
        AdminOrderDetailResponse expected = new AdminOrderDetailResponse(
                123L, "ORD-1", OrderStatus.PREPARING_SHIPMENT, null, null, null, null, null, false,
                null, List.of(), null, null);
        when(adminOrderService.findById(123L)).thenReturn(expected);

        CommonResponse<AdminOrderDetailResponse> response = adminOrderController.findById(123L);

        assertSame(expected, response.getData());
    }

    @Test
    @DisplayName("changeStatus - 상태 변경을 위임한다")
    void changeStatus() {
        AdminOrderStatusRequest request = new AdminOrderStatusRequest(OrderStatus.SHIPPED, null, null, null);
        AdminOrderStatusResponse expected = new AdminOrderStatusResponse(123L, "ORD-1", OrderStatus.SHIPPED, true, null, null, null, null);
        when(adminOrderService.changeStatus(123L, request)).thenReturn(expected);

        CommonResponse<AdminOrderStatusResponse> response = adminOrderController.changeStatus(123L, request);

        assertSame(expected, response.getData());
        verify(adminOrderService).changeStatus(123L, request);
    }

    @Test
    @DisplayName("refund - 환불 처리를 위임한다")
    void refund() {
        AdminRefundRequest request = new AdminRefundRequest(true, null, null);
        AdminRefundResponse expected = AdminRefundResponse.approved(123L, "ORD-1", null, true, false);
        when(adminOrderService.refund(123L, request)).thenReturn(expected);

        CommonResponse<AdminRefundResponse> response = adminOrderController.refund(123L, request);

        assertSame(expected, response.getData());
        verify(adminOrderService).refund(123L, request);
    }
}
