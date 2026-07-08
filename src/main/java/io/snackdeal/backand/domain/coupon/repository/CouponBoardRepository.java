package io.snackdeal.backand.domain.coupon.repository;

import io.snackdeal.backand.domain.coupon.entity.CouponBoard;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CouponBoardRepository extends JpaRepository<CouponBoard, Long> {

    Optional<CouponBoard> findByIdAndDeletedAtIsNull(Long id);

    List<CouponBoard> findByDeletedAtIsNull(Sort sort);
}
