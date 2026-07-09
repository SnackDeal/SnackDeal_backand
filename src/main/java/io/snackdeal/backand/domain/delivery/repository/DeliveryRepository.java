package io.snackdeal.backand.domain.delivery.repository;

import io.snackdeal.backand.domain.delivery.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    @Query("""
            select d
            from Delivery d
            where d.memberId = :memberId
              and d.deletedAt is null
            order by d.isDefault desc, d.id desc
            """)
    List<Delivery> findActiveByMemberId(@Param("memberId") Long memberId);

    Optional<Delivery> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByMemberIdAndDeletedAtIsNull(Long memberId);

    @Query("""
            select d
            from Delivery d
            where d.memberId = :memberId
              and d.deletedAt is null
              and d.isDefault = true
            """)
    List<Delivery> findActiveDefaultsByMemberId(@Param("memberId") Long memberId);
}
