package io.snackdeal.backand.domain.dashboard.service;

import io.snackdeal.backand.api.admin.main.dto.DashboardResponse;
import io.snackdeal.backand.domain.dashboard.repository.DashboardQueryRepository;
import io.snackdeal.backand.domain.member.repository.MemberRepository;
import io.snackdeal.backand.domain.order.entity.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 관리자 대시보드 지표 집계 여러 도메인 테이블을 읽어 파생 지표를 계산하는 읽기 전용 서비스.
 * 다른 도메인 리포지토리를 수정하지 않도록, 교차 도메인 집계는 DashboardQueryRepository(EntityManager/JPQL)로 직접 조회
 */
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
}
