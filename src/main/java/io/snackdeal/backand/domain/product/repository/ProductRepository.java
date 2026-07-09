package io.snackdeal.backand.domain.product.repository;

import io.snackdeal.backand.domain.product.entity.Product;
import io.snackdeal.backand.domain.product.entity.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("""
            select p
            from Product p
            where p.status <> :deletedStatus
              and (:keyword is null or lower(p.name) like lower(concat('%', :keyword, '%')))
              and (:categoryId is null or p.categoryId = :categoryId)
              and (:status is null or p.status = :status)
              and (:lowStock = false or p.stock <= 10)
            """)
    Page<Product> searchAdminProducts(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("status") ProductStatus status,
            @Param("lowStock") boolean lowStock,
            @Param("deletedStatus") ProductStatus deletedStatus,
            Pageable pageable
    );

    @Query("""
            select p
            from Product p
            where p.status = :activeStatus
              and (:keyword is null or lower(p.name) like lower(concat('%', :keyword, '%')))
              and (:categoryId is null or p.categoryId = :categoryId)
            """)
    Page<Product> searchUserProducts(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("activeStatus") ProductStatus activeStatus,
            Pageable pageable
    );

    // 재고 차감용 비관적 락 조회 — 주문 확정(complete)에서 사용
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);

    // 인기순 조회를 위한 데이터 삽입
    @Modifying
    @Query(value = """
        UPDATE product p
        LEFT JOIN (
            SELECT
                oi.product_id,
                SUM(oi.quantity) AS total_quantity
            FROM order_item oi
            JOIN orders o ON o.id = oi.order_id
            WHERE o.deleted_at IS NULL
              AND o.status NOT IN ('CANCELLED', 'REFUND_COMPLETED', 'PENDING_PAYMENT')
            GROUP BY oi.product_id
        ) s ON s.product_id = p.id
        SET p.recent_sales_count = COALESCE(s.total_quantity, 0)
        """, nativeQuery = true)
    void updateSoldQuantity();
}
