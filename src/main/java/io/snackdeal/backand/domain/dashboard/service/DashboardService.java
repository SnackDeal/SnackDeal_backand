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
import io.snackdeal.backand.domain.order.entity.OrderStatus;
import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
public class DashboardService {

    // 재고가 이 값 이하인 상품을 '저재고'로 집계
    private static final int LOW_STOCK_THRESHOLD = 5;

    // 매출 집계에서 제외할 주문 상태 (취소/환불)
    private static final List<OrderStatus> SALES_EXCLUDED_STATUSES =
            List.of(OrderStatus.CANCELLED, OrderStatus.REFUND_REQUESTED, OrderStatus.REFUND_COMPLETED);

    private final MemberRepository memberRepository;
    private final DashboardQueryRepository dashboardQueryRepository;

    @Cacheable(cacheNames = "dashboard:summary", key = "T(java.time.LocalDate).now()")
    @Transactional(readOnly = true)
    public DashboardResponse getSummary() {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime startOfTomorrow = startOfToday.plusDays(1);

        long todayOrderCount = dashboardQueryRepository.countOrdersBetween(startOfToday, startOfTomorrow);

        long todaySalesAmount = dashboardQueryRepository.sumSalesAmountBetween(
                startOfToday, startOfTomorrow, SALES_EXCLUDED_STATUSES);

        long newMemberCount = memberRepository.countByCreatedAtBetween(startOfToday, startOfTomorrow);

        long lowStockProductCount = dashboardQueryRepository.countLowStockProducts(LOW_STOCK_THRESHOLD);

        long pendingQnaCount = dashboardQueryRepository.countPendingQna();

        return new DashboardResponse(
                todayOrderCount,
                todaySalesAmount,
                newMemberCount,
                lowStockProductCount,
                pendingQnaCount
        );
    }

    // 기간별 신규 회원가입 추이
    @Cacheable(cacheNames = "dashboard:memberChart", key = "#startDate + '_' + #endDate")
    @Transactional(readOnly = true)
    public MemberChartResponse getMemberChart(LocalDate startDate, LocalDate endDate) {
        validateRange(startDate, endDate);
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();

        Map<LocalDate, Long> countsByDate = dashboardQueryRepository.findMemberCreatedAtBetween(start, end).stream()
                .collect(Collectors.groupingBy(LocalDateTime::toLocalDate, Collectors.counting()));

        List<DailyCountPoint> items = buildDateRange(startDate, endDate)
                .map(date -> new DailyCountPoint(date, countsByDate.getOrDefault(date, 0L)))
                .toList();

        return new MemberChartResponse(items);
    }

    // 기간별 주문 수 추이
    @Cacheable(cacheNames = "dashboard:orderChart", key = "#startDate + '_' + #endDate")
    @Transactional(readOnly = true)
    public OrderChartResponse getOrderChart(LocalDate startDate, LocalDate endDate) {
        validateRange(startDate, endDate);
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();

        Map<LocalDate, Long> countsByDate = dashboardQueryRepository.findOrderedAtBetween(start, end).stream()
                .collect(Collectors.groupingBy(LocalDateTime::toLocalDate, Collectors.counting()));

        List<DailyCountPoint> items = buildDateRange(startDate, endDate)
                .map(date -> new DailyCountPoint(date, countsByDate.getOrDefault(date, 0L)))
                .toList();

        return new OrderChartResponse(items);
    }

    // 기간별 상품판매(매출액/판매수량) 추이 (취소/환불 제외)
    @Cacheable(cacheNames = "dashboard:salesChart", key = "#startDate + '_' + #endDate")
    @Transactional(readOnly = true)
    public ProductSalesChartResponse getProductSalesChart(LocalDate startDate, LocalDate endDate) {
        validateRange(startDate, endDate);
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();

        Map<LocalDate, Long> salesByDate = dashboardQueryRepository.findSalesBetween(start, end, SALES_EXCLUDED_STATUSES).stream()
                .collect(Collectors.groupingBy(
                        row -> ((LocalDateTime) row[0]).toLocalDate(),
                        Collectors.summingLong(row -> (Long) row[1])));

        Map<LocalDate, Long> quantityByDate = dashboardQueryRepository.findSoldQuantityBetween(start, end, SALES_EXCLUDED_STATUSES).stream()
                .collect(Collectors.groupingBy(
                        row -> ((LocalDateTime) row[0]).toLocalDate(),
                        Collectors.summingLong(row -> ((Integer) row[1]).longValue())));

        List<DailySalesPoint> items = buildDateRange(startDate, endDate)
                .map(date -> new DailySalesPoint(
                        date,
                        salesByDate.getOrDefault(date, 0L),
                        quantityByDate.getOrDefault(date, 0L)))
                .toList();

        return new ProductSalesChartResponse(items);
    }

    // 기간별 쿠폰 발급/사용 추이
    @Cacheable(cacheNames = "dashboard:couponChart", key = "#startDate + '_' + #endDate")
    @Transactional(readOnly = true)
    public CouponChartResponse getCouponChart(LocalDate startDate, LocalDate endDate) {
        validateRange(startDate, endDate);
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();

        Map<LocalDate, Long> issuedByDate = dashboardQueryRepository.findCouponIssuedAtBetween(start, end).stream()
                .collect(Collectors.groupingBy(LocalDateTime::toLocalDate, Collectors.counting()));

        Map<LocalDate, Long> usedByDate = dashboardQueryRepository.findCouponUsedAtBetween(start, end).stream()
                .collect(Collectors.groupingBy(LocalDateTime::toLocalDate, Collectors.counting()));

        List<DailyCouponPoint> items = buildDateRange(startDate, endDate)
                .map(date -> new DailyCouponPoint(
                        date,
                        issuedByDate.getOrDefault(date, 0L),
                        usedByDate.getOrDefault(date, 0L)))
                .toList();

        return new CouponChartResponse(items);
    }

    // 모든 날짜를 순서대로 반환
    private Stream<LocalDate> buildDateRange(LocalDate startDate, LocalDate endDate) {
        return startDate.datesUntil(endDate.plusDays(1));
    }

    // 조회 기간 검증: 시작일/종료일 필수, 시작일 <= 종료일
    private void validateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            throw new BusinessException(ResponseCode.INVALID_DATE_RANGE);
        }
    }
}
