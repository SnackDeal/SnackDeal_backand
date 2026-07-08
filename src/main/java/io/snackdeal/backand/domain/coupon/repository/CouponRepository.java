package io.snackdeal.backand.domain.coupon.repository;

import io.snackdeal.backand.domain.coupon.entity.Coupon;
import io.snackdeal.backand.domain.coupon.entity.IssueType;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByIdAndDeletedAtIsNull(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select c
            from Coupon c
            where c.id = :couponId
              and c.deletedAt is null
            """)
    Optional<Coupon> findByIdForUpdate(@Param("couponId") Long couponId);

    @Query("""
            select c
            from Coupon c
            where c.deletedAt is null
              and (:keyword is null or lower(c.name) like lower(concat('%', :keyword, '%')))
              and (:issueType is null or c.issueType = :issueType)
            """)
    Page<Coupon> searchAdminCoupons(@Param("keyword") String keyword,
                                    @Param("issueType") IssueType issueType,
                                    Pageable pageable);

    List<Coupon> findByCouponBoardIdAndDeletedAtIsNull(Long couponBoardId);

    List<Coupon> findByCouponBoardIdAndIssueTypeAndIsActiveTrueAndDeletedAtIsNull(Long couponBoardId,
                                                                                 IssueType issueType);
}
