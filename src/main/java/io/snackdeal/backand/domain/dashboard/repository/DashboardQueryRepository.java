package io.snackdeal.backand.domain.dashboard.repository;

import io.snackdeal.backand.domain.order.entity.OrderStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 대시보드 지표 집계 전용 조회 리포지토리
 * 자체 엔티티 없이 다른 도메인 엔티티를 EntityManager(JPQL)로 직접 조회
 * (다른 도메인 리포지토리를 수정하지 않기 위함)
 */
@Repository
public class DashboardQueryRepository {

    @PersistenceContext
    private EntityManager em;

    public long countOrdersBetween(LocalDateTime start, LocalDateTime end) {
        return em.createQuery("""
                        select count(o) from Orders o
                        where o.orderedAt >= :start and o.orderedAt < :end
                        """, Long.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .getSingleResult();
    }

    public long sumSalesAmountBetween(LocalDateTime start, LocalDateTime end, List<OrderStatus> excludedStatuses) {
        return em.createQuery("""
                        select coalesce(sum(o.finalAmount), 0) from Orders o
                        where o.orderedAt >= :start and o.orderedAt < :end
                          and o.status not in :excluded
                        """, Long.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .setParameter("excluded", excludedStatuses)
                .getSingleResult();
    }

    public long countLowStockProducts(int threshold) {
        return em.createQuery("""
                        select count(p) from Product p
                        where p.stock <= :threshold and p.deletedAt is null
                        """, Long.class)
                .setParameter("threshold", threshold)
                .getSingleResult();
    }

    public long countPendingQna() {
        return em.createQuery("""
                        select count(q) from Qna q
                        where q.isAnswered = false and q.deletedAt is null
                        """, Long.class)
                .getSingleResult();
    }
}