package io.snackdeal.backand.api.user.cart.controller;

import io.snackdeal.backand.api.user.cart.dto.CartItemAddRequest;
import io.snackdeal.backand.api.user.cart.dto.CartItemDeleteRequest;
import io.snackdeal.backand.api.user.cart.dto.CartItemResponse;
import io.snackdeal.backand.api.user.cart.dto.CartItemUpdateRequest;
import io.snackdeal.backand.api.user.cart.dto.CartResponse;
import io.snackdeal.backand.api.user.member.dto.MemberDetails;
import io.snackdeal.backand.domain.cart.service.CartService;
import io.snackdeal.backand.domain.member.entity.MemberRole;
import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:test-config.properties")
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CartService cartService;

    private static final String EMAIL = "test@test.com";

    private RequestPostProcessor asUser() {
        UserDetails principal = new MemberDetails(1L, EMAIL, "password", MemberRole.USER);
        return authentication(new UsernamePasswordAuthenticationToken(principal, "token", principal.getAuthorities()));
    }

    @Test
    @DisplayName("findCart - 장바구니 조회 성공")
    void findCart_success() throws Exception {
        // given
        CartItemResponse item = new CartItemResponse(1L, 10L, "허니버터 프레첼", 1000L, 2);
        given(cartService.findCart(EMAIL)).willReturn(new CartResponse(List.of(item), 2000L));

        // when / then
        mockMvc.perform(get("/cart").with(asUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items[0].productName").value("허니버터 프레첼"))
                .andExpect(jsonPath("$.data.totalPrice").value(2000));
    }

    @Test
    @DisplayName("findCart - 인증 없이 접근 시 401")
    void findCart_withoutAuth_unauthorized() throws Exception {
        mockMvc.perform(get("/cart"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(cartService);
    }

    @Test
    @DisplayName("addItem - 장바구니 담기 성공")
    void addItem_success() throws Exception {
        // given
        CartItemAddRequest request = new CartItemAddRequest(10L, 2);
        CartItemResponse response = new CartItemResponse(1L, 10L, "허니버터 프레첼", 1000L, 2);
        given(cartService.addItem(eq(EMAIL), any(CartItemAddRequest.class))).willReturn(response);

        // when / then
        mockMvc.perform(post("/cart").with(asUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.quantity").value(2));
    }

    @Test
    @DisplayName("addItem - productId가 없으면 400 Bad Request")
    void addItem_validationFail_whenProductIdMissing() throws Exception {
        // given
        CartItemAddRequest request = new CartItemAddRequest(null, 2);

        // when / then
        mockMvc.perform(post("/cart").with(asUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(cartService);
    }

    @Test
    @DisplayName("addItem - quantity가 0 이하면 400 Bad Request")
    void addItem_validationFail_whenQuantityNotPositive() throws Exception {
        // given
        CartItemAddRequest request = new CartItemAddRequest(10L, 0);

        // when / then
        mockMvc.perform(post("/cart").with(asUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(cartService);
    }

    @Test
    @DisplayName("addItem - 재고 초과 시 422")
    void addItem_outOfStock_fail() throws Exception {
        // given
        CartItemAddRequest request = new CartItemAddRequest(10L, 100);
        given(cartService.addItem(eq(EMAIL), any(CartItemAddRequest.class)))
                .willThrow(new BusinessException(ResponseCode.CART_OUT_OF_STOCK));

        // when / then
        mockMvc.perform(post("/cart").with(asUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("CA002"));
    }

    @Test
    @DisplayName("updateQuantity - 수량 변경 성공")
    void updateQuantity_success() throws Exception {
        // given
        Long itemId = 1L;
        CartItemUpdateRequest request = new CartItemUpdateRequest(5);
        CartItemResponse response = new CartItemResponse(itemId, 10L, "허니버터 프레첼", 1000L, 5);
        given(cartService.updateQuantity(eq(EMAIL), eq(itemId), any(CartItemUpdateRequest.class)))
                .willReturn(response);

        // when / then
        mockMvc.perform(patch("/cart/{itemId}", itemId).with(asUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.quantity").value(5));
    }

    @Test
    @DisplayName("updateQuantity - quantity가 0 이하면 400 Bad Request")
    void updateQuantity_validationFail_whenQuantityNotPositive() throws Exception {
        // given
        CartItemUpdateRequest request = new CartItemUpdateRequest(0);

        // when / then
        mockMvc.perform(patch("/cart/{itemId}", 1L).with(asUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(cartService);
    }

    @Test
    @DisplayName("updateQuantity - 존재하지 않는 항목이면 404")
    void updateQuantity_itemNotFound_fail() throws Exception {
        // given
        Long itemId = 999L;
        CartItemUpdateRequest request = new CartItemUpdateRequest(5);
        given(cartService.updateQuantity(eq(EMAIL), eq(itemId), any(CartItemUpdateRequest.class)))
                .willThrow(new BusinessException(ResponseCode.CART_ITEM_NOT_FOUND));

        // when / then
        mockMvc.perform(patch("/cart/{itemId}", itemId).with(asUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CA001"));
    }

    @Test
    @DisplayName("delete - 요청 본문 없이 호출하면 전체 삭제")
    void delete_withoutBody_success() throws Exception {
        // when / then
        mockMvc.perform(delete("/cart").with(asUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(cartService).delete(EMAIL, null);
    }

    @Test
    @DisplayName("delete - id 목록을 담아 호출하면 선택 삭제")
    void delete_withIds_success() throws Exception {
        // given
        CartItemDeleteRequest request = new CartItemDeleteRequest(List.of(1L, 2L));

        // when / then
        mockMvc.perform(delete("/cart").with(asUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(cartService).delete(eq(EMAIL), any(CartItemDeleteRequest.class));
    }
}