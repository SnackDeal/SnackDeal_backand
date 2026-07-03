package io.snackdeal.backand.api.user.cart.dto;

import java.util.List;

/** 장바구니 선택 삭제 요청. */
public record CartItemDeleteRequest(
        List<Long> cartItemIds
) {
}
