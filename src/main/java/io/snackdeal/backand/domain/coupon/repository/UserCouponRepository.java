package io.snackdeal.backand.domain.coupon.repository;

import io.snackdeal.backand.domain.coupon.entity.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
}
