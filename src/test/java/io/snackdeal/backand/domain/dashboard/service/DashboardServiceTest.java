package io.snackdeal.backand.domain.dashboard.service;

import io.snackdeal.backand.api.admin.main.dto.CouponChartResponse;
import io.snackdeal.backand.api.admin.main.dto.DailyCountPoint;
import io.snackdeal.backand.api.admin.main.dto.DailyCouponPoint;
import io.snackdeal.backand.api.admin.main.dto.DailySalesPoint;
import io.snackdeal.backand.api.admin.main.dto.DashboardResponse;
import io.snackdeal.backand.api.admin.main.dto.MemberChartResponse;
import io.snackdeal.backand.api.admin.main.dto.OrderChartResponse;
import io.snackdeal.backand.api.admin.main.dto.ProductSalesChartResponse;
import io.snackdeal.backand.domain.dashboard.repository.DashboardQueryRepository;
import io.snackdeal.backand.domain.member.repository.MemberRepository;
import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @InjectMocks
    private DashboardService dashboardService;

    @Mock
    private DashboardQueryRepository dashboardQueryRepository;
    @Mock
    private MemberRepository memberRepository;

    @Test
    @DisplayName("getSummary - 각 지표를 집계해 DashboardResponse 로 매핑")
    void getSummary() {
        when(dashboardQueryRepository.countOrdersBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(12L);
        when(dashboardQueryRepository.sumSalesAmountBetween(any(LocalDateTime.class), any(LocalDateTime.class), anyList()))
                .thenReturn(340000L);
        when(dashboardQueryRepository.countLowStockProducts(anyInt()))
                .thenReturn(3L);
        when(dashboardQueryRepository.countPendingQna())
                .thenReturn(6L);
        when(memberRepository.countByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(5L);

        DashboardResponse response = dashboardService.getSummary();

        assertEquals(12, response.todayOrderCount());
        assertEquals(340000, response.todaySalesAmount());
        assertEquals(5, response.newMemberCount());
        assertEquals(3, response.lowStockProductCount());
        assertEquals(6, response.pendingQnaCount());
    }

    private static final LocalDate DAY1 = LocalDate.of(2026, 7, 1);
    private static final LocalDate DAY2 = LocalDate.of(2026, 7, 2);
    private static final LocalDate DAY3 = LocalDate.of(2026, 7, 3);

    @Test
    @DisplayName("getMemberChart - 일자별로 그룹핑하고 데이터 없는 날은 0")
    void getMemberChart() {
        when(dashboardQueryRepository.findMemberCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(
                        DAY1.atTime(9, 0),
                        DAY1.atTime(18, 0),
                        DAY3.atTime(10, 0)));

        MemberChartResponse response = dashboardService.getMemberChart(DAY1, DAY3);

        assertEquals(List.of(
                new DailyCountPoint(DAY1, 2L),
                new DailyCountPoint(DAY2, 0L),
                new DailyCountPoint(DAY3, 1L)
        ), response.items());
    }

    @Test
    @DisplayName("getOrderChart - 일자별로 그룹핑하고 데이터 없는 날은 0")
    void getOrderChart() {
        when(dashboardQueryRepository.findOrderedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(DAY2.atTime(12, 0)));

        OrderChartResponse response = dashboardService.getOrderChart(DAY1, DAY3);

        assertEquals(List.of(
                new DailyCountPoint(DAY1, 0L),
                new DailyCountPoint(DAY2, 1L),
                new DailyCountPoint(DAY3, 0L)
        ), response.items());
    }

    @Test
    @DisplayName("getProductSalesChart - 일자별 매출액/판매수량을 합산한다")
    void getProductSalesChart() {
        when(dashboardQueryRepository.findSalesBetween(any(LocalDateTime.class), any(LocalDateTime.class), anyList()))
                .thenReturn(List.of(
                        new Object[]{DAY1.atTime(9, 0), 10000L},
                        new Object[]{DAY1.atTime(15, 0), 5000L},
                        new Object[]{DAY3.atTime(9, 0), 7000L}));
        when(dashboardQueryRepository.findSoldQuantityBetween(any(LocalDateTime.class), any(LocalDateTime.class), anyList()))
                .thenReturn(List.of(
                        new Object[]{DAY1.atTime(9, 0), 2},
                        new Object[]{DAY1.atTime(15, 0), 1},
                        new Object[]{DAY3.atTime(9, 0), 3}));

        ProductSalesChartResponse response = dashboardService.getProductSalesChart(DAY1, DAY3);

        assertEquals(List.of(
                new DailySalesPoint(DAY1, 15000L, 3L),
                new DailySalesPoint(DAY2, 0L, 0L),
                new DailySalesPoint(DAY3, 7000L, 3L)
        ), response.items());
    }

    @Test
    @DisplayName("getCouponChart - 일자별 쿠폰 발급/사용 건수를 각각 집계한다")
    void getCouponChart() {
        when(dashboardQueryRepository.findCouponIssuedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(DAY1.atTime(9, 0), DAY1.atTime(10, 0), DAY2.atTime(9, 0)));
        when(dashboardQueryRepository.findCouponUsedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(DAY2.atTime(11, 0)));

        CouponChartResponse response = dashboardService.getCouponChart(DAY1, DAY3);

        assertEquals(List.of(
                new DailyCouponPoint(DAY1, 2L, 0L),
                new DailyCouponPoint(DAY2, 1L, 1L),
                new DailyCouponPoint(DAY3, 0L, 0L)
        ), response.items());
    }

    @Test
    @DisplayName("차트 조회 - 시작일이 종료일보다 늦으면 INVALID_DATE_RANGE 예외")
    void invalidDateRange() {
        BusinessException exception = assertThrows(BusinessException.class,
                () -> dashboardService.getMemberChart(DAY3, DAY1));

        assertEquals(ResponseCode.INVALID_DATE_RANGE, exception.getResponseCode());
    }
}
