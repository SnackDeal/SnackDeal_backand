package io.snackdeal.backand.domain.dashboard.service;

import io.snackdeal.backand.api.admin.main.dto.DashboardResponse;
import io.snackdeal.backand.domain.member.repository.MemberRepository;
import io.snackdeal.backand.domain.order.entity.OrderStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 관리자 대시보드 지표 집계. 여러 도메인 테이블을 읽어 파생 지표를 계산하는 읽기 전용 서비스.
 * 다른 도메인 리포지토리를 수정하지 않도록, 교차 도메인 집계는 EntityManager(JPQL)로 직접 조회한다.
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

    @PersistenceContext
    private EntityManager em;

    @Transactional(readOnly = true)
    public DashboardResponse getSummary() {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime startOfTomorrow = startOfToday.plusDays(1);

        long todayOrderCount = em.createQuery("""
                        select count(o) from Orders o
                        where o.orderedAt >= :start and o.orderedAt < :end
                        """, Long.class)
                .setParameter("start", startOfToday)
                .setParameter("end", startOfTomorrow)
                .getSingleResult();

        long todaySalesAmount = em.createQuery("""
                        select coalesce(sum(o.finalAmount), 0) from Orders o
                        where o.orderedAt >= :start and o.orderedAt < :end
                          and o.status not in :excluded
                        """, Long.class)
                .setParameter("start", startOfToday)
                .setParameter("end", startOfTomorrow)
                .setParameter("excluded", SALES_EXCLUDED_STATUSES)
                .getSingleResult();

        long newMemberCount = memberRepository.countByCreatedAtBetween(startOfToday, startOfTomorrow);

        long lowStockProductCount = em.createQuery("""
                        select count(p) from Product p
                        where p.stock <= :threshold and p.deletedAt is null
                        """, Long.class)
                .setParameter("threshold", LOW_STOCK_THRESHOLD)
                .getSingleResult();

        long pendingQnaCount = em.createQuery("""
                        select count(q) from Qna q
                        where q.isAnswered = false and q.deletedAt is null
                        """, Long.class)
                .getSingleResult();

        return new DashboardResponse(
                todayOrderCount,
                todaySalesAmount,
                newMemberCount,
                lowStockProductCount,
                pendingQnaCount
        );
    }
}
