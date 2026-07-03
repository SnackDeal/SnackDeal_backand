package io.snackdeal.backand.api.user.cart.controller;

import io.snackdeal.backand.domain.cart.service.CartService;
import io.snackdeal.backand.global.config.dto.CommonResponse;
import io.snackdeal.backand.api.user.member.dto.MemberDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public CommonResponse<Object> findCart(@AuthenticationPrincipal MemberDetails details) {
        return CommonResponse.success(cartService.findCart(details.getEmail()));
    }

    @PostMapping
    public CommonResponse<Object> addItem(@AuthenticationPrincipal MemberDetails details, @RequestBody Object request) {
        return CommonResponse.success(cartService.addItem(details.getEmail(), request));
    }

    @PatchMapping("/{itemId}")
    public CommonResponse<Object> updateQuantity(@AuthenticationPrincipal MemberDetails details,
                                                   @PathVariable Long itemId, @RequestBody Object request) {
        return CommonResponse.success(cartService.updateQuantity(details.getEmail(), itemId, request));
    }

    @DeleteMapping
    public CommonResponse<Void> delete(@AuthenticationPrincipal MemberDetails details, @RequestBody(required = false) Object request) {
        cartService.delete(details.getEmail(), request);
        return CommonResponse.success(null);
    }
}
