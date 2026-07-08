package io.snackdeal.backand.domain.cart.repository;

import io.snackdeal.backand.domain.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


//
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // 회원의 장바구니 목록 조회 (최근에 담은 순)
    List<CartItem> findAllByMemberIdOrderByCreatedAtDesc(Long memberId);

    // 동일 상품 중복 담기 판별용 — 이미 담겨 있으면 수량 합산 처리
    Optional<CartItem> findByMemberIdAndProductId(Long memberId, Long productId);

    // 수량 변경/개별 조회 시 본인 소유 항목인지 함께 검증
    Optional<CartItem> findByIdAndMemberId(Long id, Long memberId);

    // 전체 삭제 (DELETE body 없거나 빈 배열일 때)
    @Modifying
    @Query("delete from CartItem c where c.memberId = :memberId")
    int deleteAllByMemberId(@Param("memberId") Long memberId);

    // 선택 삭제 — 다른 회원의 항목이 섞여 들어와도 memberId 조건으로 방어
    @Modifying
    @Query("delete from CartItem c where c.memberId = :memberId and c.id in :ids")
    int deleteByMemberIdAndIds(@Param("memberId") Long memberId,
                               @Param("ids") Collection<Long> ids);
}