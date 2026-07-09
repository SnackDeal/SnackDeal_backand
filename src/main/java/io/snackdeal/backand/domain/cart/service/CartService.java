package io.snackdeal.backand.domain.cart.service;

import io.snackdeal.backand.api.user.cart.dto.CartItemAddRequest;
import io.snackdeal.backand.api.user.cart.dto.CartItemDeleteRequest;
import io.snackdeal.backand.api.user.cart.dto.CartItemResponse;
import io.snackdeal.backand.api.user.cart.dto.CartItemUpdateRequest;
import io.snackdeal.backand.api.user.cart.dto.CartResponse;
import io.snackdeal.backand.domain.cart.entity.CartItem;
import io.snackdeal.backand.domain.cart.repository.CartItemRepository;
import io.snackdeal.backand.domain.member.repository.MemberRepository;
import io.snackdeal.backand.domain.product.entity.Product;
import io.snackdeal.backand.domain.product.repository.ProductRepository;
import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    /** 장바구니 목록 조회 */
    public CartResponse findCart(String email){
        Long memberId = resolveMemberId(email);

        List<CartItem> items = cartItemRepository.findAllByMemberIdOrderByCreatedAtDesc(memberId);

        if(items.isEmpty()){
            //장바구니 없으면 0
            return new CartResponse(List.of(), 0L);
        }

        //N+1 방지 : 상품 한 번에 조회해서  MAP 으로 인덱싱
        List<Long> productIds = items.stream().map(CartItem::getProductId).toList();
        Map<Long, Product> productMap = productRepository.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        //상품 없으면 예외처리
        List<CartItemResponse> responses = items.stream()
                .map(it -> {
                    Product p = productMap.get(it.getProductId());
                    if (p == null) {
                        throw new BusinessException(ResponseCode.PRODUCT_NOT_FOUND);
                    }
                    return new CartItemResponse(
                            it.getId(), p.getId(), p.getName(), p.getPrice(), it.getQuantity()
                    );
                })
                .toList();

        // 총금액(price * quantity)
        long totalPrice = responses.stream()
                .mapToLong(it -> it.price() * it.quantity()).sum();


        return new CartResponse(responses, totalPrice);

    }

    /**
     * 장바구니 담기
     * 동일 상품이 있는 경우 수량 합산하고 없으면 새로 저장
     * 최종 수량이 재고 초과시 422
     */
    @Transactional
    public CartItemResponse addItem(String email, CartItemAddRequest request){
        Long memberId = resolveMemberId(email);
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new BusinessException(ResponseCode.PRODUCT_NOT_FOUND));

        CartItem item = cartItemRepository.findByMemberIdAndProductId
                (memberId, request.productId())
                .orElse(null);

        if (item == null) {
            // 첫 담기 — 요청 수량 자체가 재고를 넘으면 거절
            if (request.quantity() > product.getStock()) {
                throw new BusinessException(ResponseCode.CART_OUT_OF_STOCK);
            }
            item = cartItemRepository.save(CartItem.builder()
                    .memberId(memberId)
                    .productId(product.getId())
                    .quantity(request.quantity())
                    .build());
        } else {
            // 이미 담긴 항목 — 합산된 수량 기준으로 재고 검증
            int newQty = item.getQuantity() + request.quantity();
            if (newQty > product.getStock()) {
                throw new BusinessException(ResponseCode.CART_OUT_OF_STOCK);
            }
            item.changeQuantity(newQty);
        }

        return new CartItemResponse(
                item.getId(), product.getId(), product.getName(), product.getPrice(), item.getQuantity()
        );

    }

    /** 장바구니 수량 변경 - 요청한 수량으로 덮어씀 */
    @Transactional
    public CartItemResponse updateQuantity(String email, Long itemId, CartItemUpdateRequest request){
        Long memberId = resolveMemberId(email);

        //memberId 조건으로 타인 장바구니 항목 차단
        CartItem item = cartItemRepository.findByIdAndMemberId(itemId, memberId)
                .orElseThrow(() -> new BusinessException(ResponseCode.CART_ITEM_NOT_FOUND));

        Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new BusinessException(ResponseCode.PRODUCT_NOT_FOUND));

        //재고확인용
        if(request.quantity() > product.getStock()){
            throw new BusinessException(ResponseCode.CART_OUT_OF_STOCK);
        }
        item.changeQuantity(request.quantity());

        return new CartItemResponse(
                item.getId(), product.getId(), product.getName(), product.getPrice(), item.getQuantity()
        );
    }

    /** 장바구니 항목 삭제 */
    @Transactional
    public void delete(String email, CartItemDeleteRequest request) {
        Long memberId = resolveMemberId(email);

        if(request == null || request.cartItemIds() == null || request.cartItemIds().isEmpty()){
            cartItemRepository.deleteAllByMemberId(memberId);
            return;
        }

        cartItemRepository.deleteByMemberIdAndIds(memberId, request.cartItemIds());



    }



    /** 인증 정보 없으면 예외 */
    private Long resolveMemberId(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ResponseCode.MEMBER_NOT_FOUND))
                .getId();
    }

}
