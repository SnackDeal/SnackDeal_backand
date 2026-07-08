package io.snackdeal.backand.domain.coupon.repository;

import io.snackdeal.backand.domain.coupon.entity.UserCoupon;
import io.snackdeal.backand.domain.coupon.entity.UserCouponStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

    boolean existsByMemberIdAndCouponId(Long memberId, Long couponId);

    Optional<UserCoupon> findByIdAndMemberId(Long id, Long memberId);

    List<UserCoupon> findByMemberId(Long memberId);

    List<UserCoupon> findByMemberIdAndStatus(Long memberId, UserCouponStatus status);

    long countByCouponIdAndStatus(Long couponId, UserCouponStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select uc
            from UserCoupon uc
            where uc.id = :userCouponId
              and uc.memberId = :memberId
            """)
    Optional<UserCoupon> findByIdAndMemberIdForUpdate(@Param("userCouponId") Long userCouponId,
                                                      @Param("memberId") Long memberId);
}
