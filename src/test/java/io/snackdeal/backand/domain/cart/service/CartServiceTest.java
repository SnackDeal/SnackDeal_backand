package io.snackdeal.backand.domain.cart.service;

import io.snackdeal.backand.api.user.cart.dto.CartItemAddRequest;
import io.snackdeal.backand.api.user.cart.dto.CartItemDeleteRequest;
import io.snackdeal.backand.api.user.cart.dto.CartItemUpdateRequest;
import io.snackdeal.backand.api.user.cart.dto.CartResponse;
import io.snackdeal.backand.domain.cart.entity.CartItem;
import io.snackdeal.backand.domain.cart.repository.CartItemRepository;
import io.snackdeal.backand.domain.member.entity.Member;
import io.snackdeal.backand.domain.member.entity.MemberRole;
import io.snackdeal.backand.domain.member.repository.MemberRepository;
import io.snackdeal.backand.domain.product.entity.Product;
import io.snackdeal.backand.domain.product.entity.ProductStatus;
import io.snackdeal.backand.domain.product.repository.ProductRepository;
import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartService cartService;

    private static final String EMAIL = "test@test.com";
    private static final Long MEMBER_ID = 1L;

    private Member createMember(Long id) {
        Member member = Member.builder()
                .email(EMAIL)
                .password("password")
                .name("홍길동")
                .phone("01011112222")
                .role(MemberRole.USER)
                .build();
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }

    private Product createProduct(Long id, Long price, Integer stock) {
        Product product = Product.builder()
                .name("허니버터 프레첼")
                .price(price)
                .description("맛있음")
                .stock(stock)
                .categoryId(1L)
                .status(ProductStatus.ACTIVE)
                .build();
        ReflectionTestUtils.setField(product, "id", id);
        return product;
    }

    private CartItem createCartItem(Long id, Long memberId, Long productId, int quantity) {
        CartItem item = CartItem.builder()
                .memberId(memberId)
                .productId(productId)
                .quantity(quantity)
                .build();
        ReflectionTestUtils.setField(item, "id", id);
        return item;
    }

    @Test
    @DisplayName("findCart - 담긴 항목이 없으면 총액 0인 빈 장바구니 반환")
    void findCart_empty_returnsZeroTotal() {
        // given
        Member member = createMember(MEMBER_ID);
        given(memberRepository.findByEmail(EMAIL)).willReturn(Optional.of(member));
        given(cartItemRepository.findAllByMemberIdOrderByCreatedAtDesc(MEMBER_ID)).willReturn(List.of());

        // when
        CartResponse result = cartService.findCart(EMAIL);

        // then
        assertThat(result.items()).isEmpty();
        assertThat(result.totalPrice()).isZero();
    }

    @Test
    @DisplayName("findCart - 상품 정보와 함께 항목별 금액, 총액 계산")
    void findCart_success() {
        // given
        Member member = createMember(MEMBER_ID);
        Product product1 = createProduct(10L, 1000L, 20);
        Product product2 = createProduct(20L, 2000L, 20);
        CartItem item1 = createCartItem(1L, MEMBER_ID, 10L, 2);
        CartItem item2 = createCartItem(2L, MEMBER_ID, 20L, 3);

        given(memberRepository.findByEmail(EMAIL)).willReturn(Optional.of(member));
        given(cartItemRepository.findAllByMemberIdOrderByCreatedAtDesc(MEMBER_ID))
                .willReturn(List.of(item1, item2));
        given(productRepository.findAllById(List.of(10L, 20L))).willReturn(List.of(product1, product2));

        // when
        CartResponse result = cartService.findCart(EMAIL);

        // then — 1000*2 + 2000*3 = 8000
        assertThat(result.items()).hasSize(2);
        assertThat(result.totalPrice()).isEqualTo(8000L);
    }

    @Test
    @DisplayName("findCart - 담긴 상품이 삭제되어 조회되지 않으면 PRODUCT_NOT_FOUND 예외")
    void findCart_productMissing_fail() {
        // given
        Member member = createMember(MEMBER_ID);
        CartItem item = createCartItem(1L, MEMBER_ID, 10L, 2);

        given(memberRepository.findByEmail(EMAIL)).willReturn(Optional.of(member));
        given(cartItemRepository.findAllByMemberIdOrderByCreatedAtDesc(MEMBER_ID)).willReturn(List.of(item));
        given(productRepository.findAllById(List.of(10L))).willReturn(List.of());

        // when & then
        assertThatThrownBy(() -> cartService.findCart(EMAIL))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("responseCode", ResponseCode.PRODUCT_NOT_FOUND);
    }

    @Test
    @DisplayName("findCart - 존재하지 않는 회원이면 MEMBER_NOT_FOUND 예외")
    void findCart_memberNotFound_fail() {
        // given
        given(memberRepository.findByEmail(EMAIL)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> cartService.findCart(EMAIL))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("responseCode", ResponseCode.MEMBER_NOT_FOUND);
    }

    @Test
    @DisplayName("addItem - 처음 담는 상품이면 새 항목으로 저장")
    void addItem_newItem_success() {
        // given
        Member member = createMember(MEMBER_ID);
        Product product = createProduct(10L, 1000L, 20);
        CartItemAddRequest request = new CartItemAddRequest(10L, 3);
        CartItem saved = createCartItem(1L, MEMBER_ID, 10L, 3);

        given(memberRepository.findByEmail(EMAIL)).willReturn(Optional.of(member));
        given(productRepository.findById(10L)).willReturn(Optional.of(product));
        given(cartItemRepository.findByMemberIdAndProductId(MEMBER_ID, 10L)).willReturn(Optional.empty());
        given(cartItemRepository.save(any(CartItem.class))).willReturn(saved);

        // when
        var result = cartService.addItem(EMAIL, request);

        // then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.quantity()).isEqualTo(3);
        assertThat(result.price()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("addItem - 이미 담긴 상품이면 수량을 합산")
    void addItem_existingItem_mergesQuantity() {
        // given
        Member member = createMember(MEMBER_ID);
        Product product = createProduct(10L, 1000L, 20);
        CartItemAddRequest request = new CartItemAddRequest(10L, 3);
        CartItem existing = createCartItem(1L, MEMBER_ID, 10L, 2);

        given(memberRepository.findByEmail(EMAIL)).willReturn(Optional.of(member));
        given(productRepository.findById(10L)).willReturn(Optional.of(product));
        given(cartItemRepository.findByMemberIdAndProductId(MEMBER_ID, 10L)).willReturn(Optional.of(existing));

        // when
        var result = cartService.addItem(EMAIL, request);

        // then
        assertThat(result.quantity()).isEqualTo(5);
        verify(cartItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("addItem - 처음 담는 수량이 재고를 초과하면 CART_OUT_OF_STOCK 예외")
    void addItem_newItem_outOfStock_fail() {
        // given
        Member member = createMember(MEMBER_ID);
        Product product = createProduct(10L, 1000L, 2);
        CartItemAddRequest request = new CartItemAddRequest(10L, 3);

        given(memberRepository.findByEmail(EMAIL)).willReturn(Optional.of(member));
        given(productRepository.findById(10L)).willReturn(Optional.of(product));
        given(cartItemRepository.findByMemberIdAndProductId(MEMBER_ID, 10L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> cartService.addItem(EMAIL, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("responseCode", ResponseCode.CART_OUT_OF_STOCK);
        verify(cartItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("addItem - 합산한 수량이 재고를 초과하면 CART_OUT_OF_STOCK 예외")
    void addItem_existingItem_outOfStock_fail() {
        // given
        Member member = createMember(MEMBER_ID);
        Product product = createProduct(10L, 1000L, 4);
        CartItemAddRequest request = new CartItemAddRequest(10L, 3);
        CartItem existing = createCartItem(1L, MEMBER_ID, 10L, 2);

        given(memberRepository.findByEmail(EMAIL)).willReturn(Optional.of(member));
        given(productRepository.findById(10L)).willReturn(Optional.of(product));
        given(cartItemRepository.findByMemberIdAndProductId(MEMBER_ID, 10L)).willReturn(Optional.of(existing));

        // when & then — 2 + 3 = 5 > stock 4
        assertThatThrownBy(() -> cartService.addItem(EMAIL, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("responseCode", ResponseCode.CART_OUT_OF_STOCK);
    }

    @Test
    @DisplayName("addItem - 존재하지 않는 상품이면 PRODUCT_NOT_FOUND 예외")
    void addItem_productNotFound_fail() {
        // given
        Member member = createMember(MEMBER_ID);
        CartItemAddRequest request = new CartItemAddRequest(999L, 1);

        given(memberRepository.findByEmail(EMAIL)).willReturn(Optional.of(member));
        given(productRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> cartService.addItem(EMAIL, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("responseCode", ResponseCode.PRODUCT_NOT_FOUND);
    }

    @Test
    @DisplayName("updateQuantity - 요청한 수량으로 변경")
    void updateQuantity_success() {
        // given
        Member member = createMember(MEMBER_ID);
        Product product = createProduct(10L, 1000L, 20);
        CartItem item = createCartItem(1L, MEMBER_ID, 10L, 2);
        CartItemUpdateRequest request = new CartItemUpdateRequest(5);

        given(memberRepository.findByEmail(EMAIL)).willReturn(Optional.of(member));
        given(cartItemRepository.findByIdAndMemberId(1L, MEMBER_ID)).willReturn(Optional.of(item));
        given(productRepository.findById(10L)).willReturn(Optional.of(product));

        // when
        var result = cartService.updateQuantity(EMAIL, 1L, request);

        // then
        assertThat(result.quantity()).isEqualTo(5);
    }

    @Test
    @DisplayName("updateQuantity - 본인 소유가 아니거나 없는 항목이면 CART_ITEM_NOT_FOUND 예외")
    void updateQuantity_itemNotFound_fail() {
        // given
        Member member = createMember(MEMBER_ID);
        CartItemUpdateRequest request = new CartItemUpdateRequest(5);

        given(memberRepository.findByEmail(EMAIL)).willReturn(Optional.of(member));
        given(cartItemRepository.findByIdAndMemberId(1L, MEMBER_ID)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> cartService.updateQuantity(EMAIL, 1L, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("responseCode", ResponseCode.CART_ITEM_NOT_FOUND);
    }

    @Test
    @DisplayName("updateQuantity - 변경할 수량이 재고를 초과하면 CART_OUT_OF_STOCK 예외")
    void updateQuantity_outOfStock_fail() {
        // given
        Member member = createMember(MEMBER_ID);
        Product product = createProduct(10L, 1000L, 3);
        CartItem item = createCartItem(1L, MEMBER_ID, 10L, 2);
        CartItemUpdateRequest request = new CartItemUpdateRequest(5);

        given(memberRepository.findByEmail(EMAIL)).willReturn(Optional.of(member));
        given(cartItemRepository.findByIdAndMemberId(1L, MEMBER_ID)).willReturn(Optional.of(item));
        given(productRepository.findById(10L)).willReturn(Optional.of(product));

        // when & then
        assertThatThrownBy(() -> cartService.updateQuantity(EMAIL, 1L, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("responseCode", ResponseCode.CART_OUT_OF_STOCK);
    }

    @Test
    @DisplayName("delete - 요청 없이 호출하면 회원의 장바구니 전체 삭제")
    void delete_withoutRequest_deletesAll() {
        // given
        Member member = createMember(MEMBER_ID);
        given(memberRepository.findByEmail(EMAIL)).willReturn(Optional.of(member));

        // when
        cartService.delete(EMAIL, null);

        // then
        verify(cartItemRepository).deleteAllByMemberId(MEMBER_ID);
        verify(cartItemRepository, never()).deleteByMemberIdAndIds(any(), any());
    }

    @Test
    @DisplayName("delete - 삭제할 id 목록이 빈 배열이면 전체 삭제")
    void delete_emptyIds_deletesAll() {
        // given
        Member member = createMember(MEMBER_ID);
        given(memberRepository.findByEmail(EMAIL)).willReturn(Optional.of(member));

        // when
        cartService.delete(EMAIL, new CartItemDeleteRequest(List.of()));

        // then
        verify(cartItemRepository).deleteAllByMemberId(MEMBER_ID);
    }

    @Test
    @DisplayName("delete - id 목록이 있으면 해당 항목만 선택 삭제")
    void delete_withIds_deletesSelected() {
        // given
        Member member = createMember(MEMBER_ID);
        List<Long> ids = List.of(1L, 2L);
        given(memberRepository.findByEmail(EMAIL)).willReturn(Optional.of(member));

        // when
        cartService.delete(EMAIL, new CartItemDeleteRequest(ids));

        // then
        verify(cartItemRepository).deleteByMemberIdAndIds(MEMBER_ID, ids);
        verify(cartItemRepository, never()).deleteAllByMemberId(any());
    }
}