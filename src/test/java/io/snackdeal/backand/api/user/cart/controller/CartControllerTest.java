package io.snackdeal.backand.api.user.cart.controller;

import io.snackdeal.backand.domain.cart.service.CartService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.fail;

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

    @Disabled("TODO: implement")
    @Test
    @DisplayName("findCart - TODO")
    void findCart_Success() throws Exception {
        fail("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("addItem - TODO")
    void addItem_Success() throws Exception {
        fail("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("updateQuantity - TODO")
    void updateQuantity_Success() throws Exception {
        fail("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("delete - TODO")
    void delete_Success() throws Exception {
        fail("not implemented");
    }

}