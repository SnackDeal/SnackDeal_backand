package io.snackdeal.backand.domain.dashboard.repository;

import io.snackdeal.backand.domain.order.entity.OrderStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


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

    // 기간 내 회원 가입 시각 목록 (일자별 신규회원 차트 집계용)
    public List<LocalDateTime> findMemberCreatedAtBetween(LocalDateTime start, LocalDateTime end) {
        return em.createQuery("""
                        select m.createdAt from Member m
                        where m.createdAt >= :start and m.createdAt < :end
                        """, LocalDateTime.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }

    // 기간 내 주문 발생 시각 목록 (일자별 주문 수 차트 집계용)
    public List<LocalDateTime> findOrderedAtBetween(LocalDateTime start, LocalDateTime end) {
        return em.createQuery("""
                        select o.orderedAt from Orders o
                        where o.orderedAt >= :start and o.orderedAt < :end
                        """, LocalDateTime.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }

    // 기간 내 주문별 (주문시각, 매출액) 목록 (일자별 매출 차트 집계용, 취소/환불 제외)
    public List<Object[]> findSalesBetween(LocalDateTime start, LocalDateTime end, List<OrderStatus> excludedStatuses) {
        return em.createQuery("""
                        select o.orderedAt, o.finalAmount from Orders o
                        where o.orderedAt >= :start and o.orderedAt < :end
                          and o.status not in :excluded
                        """, Object[].class)
                .setParameter("start", start)
                .setParameter("end", end)
                .setParameter("excluded", excludedStatuses)
                .getResultList();
    }

    // 기간 내 주문상품별 (주문시각, 판매수량) 목록 (일자별 판매수량 차트 집계용, 취소/환불 제외)
    public List<Object[]> findSoldQuantityBetween(LocalDateTime start, LocalDateTime end, List<OrderStatus> excludedStatuses) {
        return em.createQuery("""
                        select o.orderedAt, oi.quantity from Orders o, OrderItem oi
                        where oi.orderId = o.id
                          and o.orderedAt >= :start and o.orderedAt < :end
                          and o.status not in :excluded
                        """, Object[].class)
                .setParameter("start", start)
                .setParameter("end", end)
                .setParameter("excluded", excludedStatuses)
                .getResultList();
    }

    // 기간 내 쿠폰 발급 시각 목록 (일자별 쿠폰 발급 차트 집계용)
    public List<LocalDateTime> findCouponIssuedAtBetween(LocalDateTime start, LocalDateTime end) {
        return em.createQuery("""
                        select uc.issuedAt from UserCoupon uc
                        where uc.issuedAt >= :start and uc.issuedAt < :end
                        """, LocalDateTime.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }

    // 기간 내 쿠폰 사용 시각 목록 (일자별 쿠폰 사용 차트 집계용)
    public List<LocalDateTime> findCouponUsedAtBetween(LocalDateTime start, LocalDateTime end) {
        return em.createQuery("""
                        select uc.usedAt from UserCoupon uc
                        where uc.usedAt >= :start and uc.usedAt < :end
                        """, LocalDateTime.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }
}