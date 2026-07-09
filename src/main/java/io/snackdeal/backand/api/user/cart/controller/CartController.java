package io.snackdeal.backand.api.user.cart.controller;

import io.snackdeal.backand.api.user.cart.dto.*;
import io.snackdeal.backand.domain.cart.service.CartService;
import io.snackdeal.backand.global.config.dto.CommonResponse;
import io.snackdeal.backand.api.user.member.dto.MemberDetails;
import io.snackdeal.backand.global.swagger.CartApiDocs;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@CartApiDocs.Doc
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @CartApiDocs.FindCart
    @GetMapping
    public CommonResponse<CartResponse> findCart(@AuthenticationPrincipal MemberDetails details) {
        return CommonResponse.success(cartService.findCart(details.getEmail()));
    }

    @CartApiDocs.AddItem
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<CartItemResponse> addItem(
            @AuthenticationPrincipal MemberDetails details,
            @RequestBody @Valid CartItemAddRequest request
    ) {
        return CommonResponse.success(cartService.addItem(details.getEmail(), request));
    }

    @CartApiDocs.UpdateQuantity
    @PatchMapping("/{itemId}")
    public CommonResponse<CartItemResponse> updateQuantity(
            @AuthenticationPrincipal MemberDetails details,
            @PathVariable Long itemId,
            @RequestBody @Valid CartItemUpdateRequest request
    ) {
        return CommonResponse.success(cartService.updateQuantity(details.getEmail(), itemId, request));
    }

    @CartApiDocs.Delete
    @DeleteMapping
    public CommonResponse<Void> delete(
            @AuthenticationPrincipal MemberDetails details,
            @RequestBody(required = false) CartItemDeleteRequest request
    ) {
        cartService.delete(details.getEmail(), request);
        return CommonResponse.success(null);
    }
}
