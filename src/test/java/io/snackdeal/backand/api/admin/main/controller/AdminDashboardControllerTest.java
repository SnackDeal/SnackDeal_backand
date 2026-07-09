package io.snackdeal.backand.api.admin.main.controller;

import io.snackdeal.backand.api.admin.main.dto.CouponChartResponse;
import io.snackdeal.backand.api.admin.main.dto.DailyCountPoint;
import io.snackdeal.backand.api.admin.main.dto.DailyCouponPoint;
import io.snackdeal.backand.api.admin.main.dto.DailySalesPoint;
import io.snackdeal.backand.api.admin.main.dto.DashboardResponse;
import io.snackdeal.backand.api.admin.main.dto.MemberChartResponse;
import io.snackdeal.backand.api.admin.main.dto.OrderChartResponse;
import io.snackdeal.backand.api.admin.main.dto.ProductSalesChartResponse;
import io.snackdeal.backand.domain.dashboard.service.DashboardService;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminDashboardControllerTest {

    @InjectMocks
    private AdminDashboardController adminDashboardController;

    @Mock
    private DashboardService dashboardService;

    @Test
    @DisplayName("main - 대시보드 집계 결과를 그대로 감싸 반환")
    void main() {
        DashboardResponse expected = new DashboardResponse(12, 340000, 5, 3, 6);
        when(dashboardService.getSummary()).thenReturn(expected);

        CommonResponse<DashboardResponse> response = adminDashboardController.main();

        assertTrue(response.isSuccess());
        assertSame(expected, response.getData());
    }

    private static final LocalDate START = LocalDate.of(2026, 7, 1);
    private static final LocalDate END = LocalDate.of(2026, 7, 3);

    @Test
    @DisplayName("memberChart - 회원 차트 집계 결과를 그대로 감싸 반환")
    void memberChart() {
        MemberChartResponse expected = new MemberChartResponse(List.of(new DailyCountPoint(START, 2L)));
        when(dashboardService.getMemberChart(START, END)).thenReturn(expected);

        CommonResponse<MemberChartResponse> response = adminDashboardController.memberChart(START, END);

        assertTrue(response.isSuccess());
        assertSame(expected, response.getData());
    }

    @Test
    @DisplayName("orderChart - 주문 차트 집계 결과를 그대로 감싸 반환")
    void orderChart() {
        OrderChartResponse expected = new OrderChartResponse(List.of(new DailyCountPoint(START, 3L)));
        when(dashboardService.getOrderChart(START, END)).thenReturn(expected);

        CommonResponse<OrderChartResponse> response = adminDashboardController.orderChart(START, END);

        assertTrue(response.isSuccess());
        assertSame(expected, response.getData());
    }

    @Test
    @DisplayName("productSalesChart - 상품판매 차트 집계 결과를 그대로 감싸 반환")
    void productSalesChart() {
        ProductSalesChartResponse expected = new ProductSalesChartResponse(List.of(new DailySalesPoint(START, 10000L, 2L)));
        when(dashboardService.getProductSalesChart(START, END)).thenReturn(expected);

        CommonResponse<ProductSalesChartResponse> response = adminDashboardController.productSalesChart(START, END);

        assertTrue(response.isSuccess());
        assertSame(expected, response.getData());
    }

    @Test
    @DisplayName("couponChart - 쿠폰 차트 집계 결과를 그대로 감싸 반환")
    void couponChart() {
        CouponChartResponse expected = new CouponChartResponse(List.of(new DailyCouponPoint(START, 2L, 1L)));
        when(dashboardService.getCouponChart(START, END)).thenReturn(expected);

        CommonResponse<CouponChartResponse> response = adminDashboardController.couponChart(START, END);

        assertTrue(response.isSuccess());
        assertSame(expected, response.getData());
    }
}
