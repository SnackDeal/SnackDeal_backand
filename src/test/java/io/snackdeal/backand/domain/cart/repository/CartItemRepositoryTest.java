package io.snackdeal.backand.domain.cart.repository;

import io.snackdeal.backand.domain.cart.entity.CartItem;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CartItemRepositoryTest {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private EntityManager entityManager;

    private CartItem saveItem(Long memberId, Long productId, int quantity) {
        return cartItemRepository.save(CartItem.builder()
                .memberId(memberId)
                .productId(productId)
                .quantity(quantity)
                .build());
    }

    @Test
    @DisplayName("findAllByMemberIdOrderByCreatedAtDesc - 최근에 담은 순으로 조회, 다른 회원 항목은 제외")
    void findAllByMemberIdOrderByCreatedAtDesc_success() {
        // given
        CartItem first = saveItem(1L, 10L, 1);
        CartItem second = saveItem(1L, 20L, 1);
        saveItem(2L, 30L, 1); // 다른 회원의 항목

        // when
        List<CartItem> result = cartItemRepository.findAllByMemberIdOrderByCreatedAtDesc(1L);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(CartItem::getId)
                .containsExactly(second.getId(), first.getId());
    }

    @Test
    @DisplayName("findByMemberIdAndProductId - 동일 회원, 동일 상품 조합만 조회")
    void findByMemberIdAndProductId_success() {
        // given
        saveItem(1L, 10L, 2);

        // when
        Optional<CartItem> found = cartItemRepository.findByMemberIdAndProductId(1L, 10L);
        Optional<CartItem> notFound = cartItemRepository.findByMemberIdAndProductId(1L, 999L);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getQuantity()).isEqualTo(2);
        assertThat(notFound).isEmpty();
    }

    @Test
    @DisplayName("findByIdAndMemberId - 본인 소유 항목만 조회되고 타인 항목은 조회되지 않음")
    void findByIdAndMemberId_success() {
        // given
        CartItem item = saveItem(1L, 10L, 2);

        // when
        Optional<CartItem> own = cartItemRepository.findByIdAndMemberId(item.getId(), 1L);
        Optional<CartItem> others = cartItemRepository.findByIdAndMemberId(item.getId(), 2L);

        // then
        assertThat(own).isPresent();
        assertThat(others).isEmpty();
    }

    @Test
    @DisplayName("deleteAllByMemberId - 해당 회원의 항목만 전체 삭제되고 다른 회원 항목은 유지")
    void deleteAllByMemberId_success() {
        // given
        saveItem(1L, 10L, 1);
        saveItem(1L, 20L, 1);
        CartItem other = saveItem(2L, 30L, 1);

        // when — 벌크 삭제는 영속성 컨텍스트에 반영되지 않으므로 clear 필요
        int deletedCount = cartItemRepository.deleteAllByMemberId(1L);
        entityManager.clear();

        // then
        assertThat(deletedCount).isEqualTo(2);
        assertThat(cartItemRepository.findAllByMemberIdOrderByCreatedAtDesc(1L)).isEmpty();
        assertThat(cartItemRepository.findById(other.getId())).isPresent();
    }

    @Test
    @DisplayName("deleteByMemberIdAndIds - 지정한 id 중 본인 소유 항목만 삭제")
    void deleteByMemberIdAndIds_success() {
        // given
        CartItem item1 = saveItem(1L, 10L, 1);
        CartItem item2 = saveItem(1L, 20L, 1);
        CartItem otherMemberItem = saveItem(2L, 30L, 1);

        // when — otherMemberItem의 id가 섞여 들어와도 memberId 조건으로 방어되어 삭제되지 않음
        int deletedCount = cartItemRepository.deleteByMemberIdAndIds(
                1L, List.of(item1.getId(), otherMemberItem.getId()));
        entityManager.clear();

        // then
        assertThat(deletedCount).isEqualTo(1);
        assertThat(cartItemRepository.findById(item1.getId())).isEmpty();
        assertThat(cartItemRepository.findById(item2.getId())).isPresent();
        assertThat(cartItemRepository.findById(otherMemberItem.getId())).isPresent();
    }
}