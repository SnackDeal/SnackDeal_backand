package io.snackdeal.backand.domain.coupon.repository;

import io.snackdeal.backand.domain.coupon.entity.CouponBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponBoardRepository extends JpaRepository<CouponBoard, Long> {
}
