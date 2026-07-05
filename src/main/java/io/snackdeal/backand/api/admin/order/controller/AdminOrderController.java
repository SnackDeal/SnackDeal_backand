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
import io.snackdeal.backand.global.swagger.AdminOrderApiDocs;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 관리자 주문 관리 API. 주문 리스트/상세 조회, 상태 변경, 환불 처리를 담당한다.
 * "/admin/**" 는 SecurityConfig 에서 ROLE_ADMIN 으로 보호된다.
 */
@AdminOrderApiDocs.Doc
@RestController
@RequestMapping("/admin/order")
@RequiredArgsConstructor
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    @AdminOrderApiDocs.AdminOrderList
    @GetMapping
    public CommonResponse<AdminOrderListResponse> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        // 기간 필터는 날짜만 받아 [시작일 00:00, 종료일 23:59:59.999] 범위로 변환한다.
        LocalDateTime from = (dateFrom != null) ? dateFrom.atStartOfDay() : null;
        LocalDateTime to = (dateTo != null) ? dateTo.atTime(LocalTime.MAX) : null;
        return CommonResponse.success(adminOrderService.findList(keyword, status, from, to, page, size));
    }

    @AdminOrderApiDocs.AdminOrderDetail
    @GetMapping("/{id}")
    public CommonResponse<AdminOrderDetailResponse> findById(@PathVariable Long id) {
        return CommonResponse.success(adminOrderService.findById(id));
    }

    @AdminOrderApiDocs.AdminOrderChangeStatus
    @PatchMapping("/{id}/status")
    public CommonResponse<AdminOrderStatusResponse> changeStatus(@PathVariable Long id,
                                                                 @Valid @RequestBody AdminOrderStatusRequest request) {
        return CommonResponse.success(adminOrderService.changeStatus(id, request));
    }

    @AdminOrderApiDocs.AdminOrderRefund
    @PostMapping("/{id}/refund")
    public CommonResponse<AdminRefundResponse> refund(@PathVariable Long id,
                                                      @Valid @RequestBody AdminRefundRequest request) {
        return CommonResponse.success(adminOrderService.refund(id, request));
    }
}
