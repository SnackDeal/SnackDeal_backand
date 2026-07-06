package io.snackdeal.backand.domain.order.repository;

import io.snackdeal.backand.domain.order.entity.OrderStatus;
import io.snackdeal.backand.domain.order.entity.Orders;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    // 주문번호(merchant_uid)로 조회 — 결제 검증(complete) 시 사용.
    Optional<Orders> findByOrderNumber(String orderNumber);

    // 주문 확정 중복 방지용 비관적 락 조회
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from Orders o where o.orderNumber = :orderNumber")
    Optional<Orders> findByOrderNumberForUpdate(@Param("orderNumber") String orderNumber);

    // 내 주문내역 (최신순 페이징).
    Page<Orders> findByMemberIdOrderByOrderedAtDesc(Long memberId, Pageable pageable);

    // 회원 누적 주문 수 (관리자 상세의 buyer.total_order_count).
    long countByMemberId(Long memberId);

    /*
     * 관리자 주문 리스트 검색.
     * keyword: 주문번호 부분검색 또는 buyer(memberId) 매칭 buyer 검색은 서비스에서 회원 id 목록으로 풀어 넘긴다.
     * status/dateFrom/dateTo 는 선택값이라 null 이면 해당 조건은 무시됨
     * memberIds 는 IN 절 특성상 비어 있으면 안 되므로 서비스에서 최소 1개(sentinel)를 채워 넘긴다.
     */
    @Query("""
            select o from Orders o
            where (:keyword is null
                   or o.orderNumber like concat('%', :keyword, '%')
                   or o.memberId in :memberIds)
              and (:status is null or o.status = :status)
              and (:dateFrom is null or o.orderedAt >= :dateFrom)
              and (:dateTo is null or o.orderedAt <= :dateTo)
            order by o.orderedAt desc
            """)
    Page<Orders> search(@Param("keyword") String keyword,
                        @Param("memberIds") List<Long> memberIds,
                        @Param("status") OrderStatus status,
                        @Param("dateFrom") LocalDateTime dateFrom,
                        @Param("dateTo") LocalDateTime dateTo,
                        Pageable pageable);
}
