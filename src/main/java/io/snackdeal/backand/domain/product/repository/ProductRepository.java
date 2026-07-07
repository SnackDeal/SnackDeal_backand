package io.snackdeal.backand.domain.product.repository;

import io.snackdeal.backand.domain.product.entity.Product;
import io.snackdeal.backand.domain.product.entity.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
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
}
