package io.snackdeal.backand.api.user.product.controller;

import io.snackdeal.backand.api.user.product.dto.ProductResponse;
import io.snackdeal.backand.api.user.product.dto.ProductSummaryResponse;
import io.snackdeal.backand.domain.product.entity.ProductStatus;
import io.snackdeal.backand.domain.product.service.ProductService;
import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import io.snackdeal.backand.global.util.PageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(
        locations = "classpath:test-config.properties",
        properties = {
                "custom.cloud.s3.access-key=test-access-key",
                "custom.cloud.s3.secret-key=test-secret-key",
                "custom.cloud.s3.bucket=test-bucket"
        }
)
@DisplayName("ProductController 클래스의")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Nested
    @DisplayName("Describe: list() 메서드는")
    class Describe_list {

        @Nested
        @DisplayName("Context: 상품 목록 조회를 요청한 경우")
        class Context_with_search_condition {

            @Test
            @DisplayName("It: 사용자 상품 목록 반환")
            void It_사용자_상품_목록_반환() throws Exception {
                // given
                ProductSummaryResponse item = new ProductSummaryResponse(
                        1L,
                        "대-정수의 핵불닭맛 감자칩",
                        50000L,
                        "https://image.test.spicy-bigint.jpg",
                        3L,
                        "과자",
                        false
                );

                given(productService.findList(
                        eq("감자"),
                        eq(3L),
                        eq("popular"),
                        eq(1),
                        eq(10)
                )).willReturn(new PageResponse<>(List.of(item), 1, 10, 1));

                // when / then
                mockMvc.perform(get("/product/list")
                                .param("keyword", "감자")
                                .param("categoryId", "3")
                                .param("sort", "popular")
                                .param("page", "1")
                                .param("size", "10"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.code").value("S000"))
                        .andExpect(jsonPath("$.data.items[0].id").value(1))
                        .andExpect(jsonPath("$.data.items[0].name").value("대-정수의 핵불닭맛 감자칩"))
                        .andExpect(jsonPath("$.data.items[0].price").value(50000))
                        .andExpect(jsonPath("$.data.items[0].thumbnailUrl").value("https://image.test.spicy-bigint.jpg"))
                        .andExpect(jsonPath("$.data.items[0].categoryId").value(3))
                        .andExpect(jsonPath("$.data.items[0].category").value("과자"))
                        .andExpect(jsonPath("$.data.items[0].isSoldout").value(false))
                        .andExpect(jsonPath("$.data.page").value(1))
                        .andExpect(jsonPath("$.data.size").value(10))
                        .andExpect(jsonPath("$.data.total").value(1));
            }
        }
    }

    @Nested
    @DisplayName("Describe: findById() 메서드는")
    class Describe_findById {

        @Nested
        @DisplayName("Context: 상품이 존재하는 경우")
        class Context_existing_product {

            @Test
            @DisplayName("It: 사용자 상품 상세 정보 반환")
            void It_사용자_상품_상세_정보_반환() throws Exception {
                // given
                given(productService.findById(1L))
                        .willReturn(createProductResponse());

                // when / then
                mockMvc.perform(get("/product/{productId}", 1L))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.code").value("S000"))
                        .andExpect(jsonPath("$.data.id").value(1))
                        .andExpect(jsonPath("$.data.name").value("대-정수의 핵불닭맛 감자칩"))
                        .andExpect(jsonPath("$.data.price").value(50000))
                        .andExpect(jsonPath("$.data.description").value("대-정수는 쉽게 먹는 핵불닭맛의 엄청 매운 감자칩"))
                        .andExpect(jsonPath("$.data.imageUrl").value("https://image.test.spicy-bigint.jpg"))
                        .andExpect(jsonPath("$.data.stock").value(10))
                        .andExpect(jsonPath("$.data.status").value("ACTIVE"))
                        .andExpect(jsonPath("$.data.isSoldout").value(false))
                        .andExpect(jsonPath("$.data.categoryId").value(3))
                        .andExpect(jsonPath("$.data.category").value("과자"));
            }
        }

        @Nested
        @DisplayName("Context: 상품이 존재하지 않는 경우")
        class Context_missing_product {

            @Test
            @DisplayName("It: 404 Not Found를 반환")
            void It_404_Not_Found_반환() throws Exception {
                // given
                given(productService.findById(999L))
                        .willThrow(new BusinessException(ResponseCode.PRODUCT_NOT_FOUND));

                // when / then
                mockMvc.perform(get("/product/{productId}", 999L))
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.success").value(false))
                        .andExpect(jsonPath("$.code").value(ResponseCode.PRODUCT_NOT_FOUND.getCode()))
                        .andExpect(jsonPath("$.message").value(ResponseCode.PRODUCT_NOT_FOUND.getMessage()));
            }
        }
    }

    private ProductResponse createProductResponse() {
        return new ProductResponse(
                1L,
                "대-정수의 핵불닭맛 감자칩",
                50000L,
                "대-정수는 쉽게 먹는 핵불닭맛의 엄청 매운 감자칩",
                "https://image.test.spicy-bigint.jpg",
                10,
                ProductStatus.ACTIVE,
                false,
                3L,
                "과자"
        );
    }
}
