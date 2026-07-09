package io.snackdeal.backand.domain.coupon.repository;

import io.snackdeal.backand.domain.coupon.entity.CouponBoard;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CouponBoardRepository extends JpaRepository<CouponBoard, Long> {

    Optional<CouponBoard> findByIdAndDeletedAtIsNull(Long id);

    List<CouponBoard> findByDeletedAtIsNull(Sort sort);

    @Query("""
            select cb
            from CouponBoard cb
            where cb.deletedAt is null
              and cb.isActive = true
              and cb.startAt <= :now
              and (cb.endAt is null or cb.endAt >= :now)
            order by cb.startAt desc, cb.createdAt desc
            """)
    List<CouponBoard> findOpenBoards(@Param("now") LocalDateTime now);

    @Query("""
            select cb
            from CouponBoard cb
            where cb.id = :boardId
              and cb.deletedAt is null
              and cb.isActive = true
              and cb.startAt <= :now
              and (cb.endAt is null or cb.endAt >= :now)
            """)
    Optional<CouponBoard> findOpenBoardById(@Param("boardId") Long boardId,
                                            @Param("now") LocalDateTime now);
}
