package io.snackdeal.backand.domain.coupon.repository;

import io.snackdeal.backand.domain.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
}
