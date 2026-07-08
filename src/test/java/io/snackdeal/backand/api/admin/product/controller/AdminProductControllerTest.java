package io.snackdeal.backand.api.admin.product.controller;

import io.snackdeal.backand.api.admin.product.dto.AdminProductDetailResponse;
import io.snackdeal.backand.api.admin.product.dto.AdminProductListResponse;
import io.snackdeal.backand.api.admin.product.dto.AdminProductRequest;
import io.snackdeal.backand.api.admin.product.dto.AdminProductStatusResponse;
import io.snackdeal.backand.api.admin.product.dto.AdminProductStatusUpdateRequest;
import io.snackdeal.backand.api.user.member.dto.MemberDetails;
import io.snackdeal.backand.domain.member.entity.MemberRole;
import io.snackdeal.backand.domain.product.entity.ProductStatus;
import io.snackdeal.backand.domain.product.service.AdminProductService;
import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import io.snackdeal.backand.global.util.PageResponse;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
@DisplayName("AdminProductController 클래스의")
class AdminProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AdminProductService adminProductService;

    @Nested
    @DisplayName("Describe: findList() 메서드는")
    class Describe_list {

        @Nested
        @DisplayName("Context: 관리자 권한으로 상품 목록 조회 요청한 경우")
        class Context_with_admin_role {

            @Test
            @DisplayName("It: 관리자 상품 목록 반환")
            void It_관리자_상품_목록을_반환() throws Exception {
                // given
                AdminProductListResponse item = new AdminProductListResponse(
                        1L,
                        "대-정수의 핵불닭맛 감자칩",
                        3L,
                        "과자",
                        50000L,
                        10,
                        ProductStatus.ACTIVE,
                        "https://image.test.spicy-bigint.jpg"
                );

                given(adminProductService.findList(
                        eq("감자"),
                        eq(3L),
                        eq(ProductStatus.ACTIVE),
                        eq(false),
                        eq("latest"),
                        eq(1),
                        eq(10)
                )).willReturn(new PageResponse<>(List.of(item), 1, 10, 1));

                // when & then
                mockMvc.perform(get("/admin/product")
                                .with(as(MemberRole.ADMIN))
                                .param("keyword", "감자")
                                .param("categoryId", "3")
                                .param("status", "ACTIVE")
                                .param("lowStock", "false")
                                .param("sort", "latest")
                                .param("page", "1")
                                .param("size", "10"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.data.items[0].id").value(1))
                        .andExpect(jsonPath("$.data.items[0].name").value("대-정수의 핵불닭맛 감자칩"))
                        .andExpect(jsonPath("$.data.items[0].categoryId").value(3))
                        .andExpect(jsonPath("$.data.items[0].category").value("과자"))
                        .andExpect(jsonPath("$.data.items[0].price").value(50000))
                        .andExpect(jsonPath("$.data.items[0].stock").value(10))
                        .andExpect(jsonPath("$.data.items[0].status").value("ACTIVE"))
                        .andExpect(jsonPath("$.data.items[0].thumbnailUrl").value("https://image.test.spicy-bigint.jpg"))
                        .andExpect(jsonPath("$.data.page").value(1))
                        .andExpect(jsonPath("$.data.size").value(10))
                        .andExpect(jsonPath("$.data.total").value(1));
            }
        }
    }

    @Nested
    @DisplayName("Describe: save() 메서드는")
    class Describe_save {

        @Nested
        @DisplayName("Context: 올바른 상품 등록 요청인 경우")
        class Context_valid_request {

            @Test
            @DisplayName("It: 상품을 등록하고 생성 응답 반환")
            void It_상품을_등록하고_생성_응답을_반환한다() throws Exception {
                // given
                AdminProductRequest request = createRequest(ProductStatus.ACTIVE);
                AdminProductDetailResponse response = createDetailResponse(ProductStatus.ACTIVE);

                given(adminProductService.save(any(AdminProductRequest.class)))
                        .willReturn(response);

                // when & then
                mockMvc.perform(post("/admin/product")
                                .with(as(MemberRole.ADMIN))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.code").value("S001"))
                        .andExpect(jsonPath("$.data.id").value(1))
                        .andExpect(jsonPath("$.data.name").value("대-정수의 핵불닭맛 감자칩"))
                        .andExpect(jsonPath("$.data.categoryId").value(3))
                        .andExpect(jsonPath("$.data.category").value("과자"))
                        .andExpect(jsonPath("$.data.price").value(50000))
                        .andExpect(jsonPath("$.data.stock").value(10))
                        .andExpect(jsonPath("$.data.status").value("ACTIVE"))
                        .andExpect(jsonPath("$.data.imageUrl").value("https://image.test.spicy-bigint.jpg"));
            }
        }

        @Nested
        @DisplayName("Context: 상품명이 빈 문자열인 경우")
        class Context_blank_name {

            @Test
            @DisplayName("It: 400 Bad Request를 반환한다")
            void It_400_Bad_Request_반환() throws Exception {
                // given
                AdminProductRequest request = new AdminProductRequest(
                        "",
                        50000L,
                        3L,
                        "대-정수는 쉽게 먹는 핵불닭맛의 엄청 매운 감자칩",
                        10,
                        "https://image.test.spicy-bigint.jpg",
                        ProductStatus.ACTIVE
                );

                // when & then
                mockMvc.perform(post("/admin/product")
                                .with(as(MemberRole.ADMIN))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isBadRequest());
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
            @DisplayName("It: 관리자 상품 상세 정보를 반환")
            void It_관리자_상품_상세_정보를_반환() throws Exception {
                // given
                given(adminProductService.findById(1L))
                        .willReturn(createDetailResponse(ProductStatus.INACTIVE));

                // when & then
                mockMvc.perform(get("/admin/product/{id}", 1L)
                                .with(as(MemberRole.ADMIN)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.data.id").value(1))
                        .andExpect(jsonPath("$.data.name").value("대-정수의 핵불닭맛 감자칩"))
                        .andExpect(jsonPath("$.data.status").value("INACTIVE"))
                        .andExpect(jsonPath("$.data.category").value("과자"));
            }
        }

        @Nested
        @DisplayName("Context: 상품이 존재하지 않는 경우")
        class Context_missing_product {

            @Test
            @DisplayName("It: 404 Not Found를 반환")
            void It_404를_반환한다() throws Exception {
                // given
                given(adminProductService.findById(999L))
                        .willThrow(new BusinessException(ResponseCode.PRODUCT_NOT_FOUND));

                // when & then
                mockMvc.perform(get("/admin/product/{id}", 999L)
                                .with(as(MemberRole.ADMIN)))
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.success").value(false))
                        .andExpect(jsonPath("$.code").value(ResponseCode.PRODUCT_NOT_FOUND.getCode()))
                        .andExpect(jsonPath("$.message").value(ResponseCode.PRODUCT_NOT_FOUND.getMessage()));
            }
        }
    }

    @Nested
    @DisplayName("Describe: update() 메서드는")
    class Describe_update {

        @Nested
        @DisplayName("Context: 올바른 상품 수정 요청인 경우")
        class Context_valid_request {

            @Test
            @DisplayName("It: 상품을 수정하고 상세 응답 반환")
            void It_상품을_수정하고_상세_응답_반환() throws Exception {
                // given
                AdminProductRequest request = new AdminProductRequest(
                        "수정된 대-정수의 핵불닭맛 감자칩",
                        2000L,
                        4L,
                        "BIGINT는 쉽게 먹는 핵불닭맛의 엄청 매운 감자칩",
                        20,
                        "https://image.test.update.spicy-bigint.jpg",
                        ProductStatus.INACTIVE
                );

                AdminProductDetailResponse response = new AdminProductDetailResponse(
                        1L,
                        "수정된 대-정수의 핵불닭맛 감자칩",
                        4L,
                        "신규 과자",
                        2000L,
                        "BIGINT는 쉽게 먹는 핵불닭맛의 엄청 매운 감자칩",
                        20,
                        ProductStatus.INACTIVE,
                        "https://image.test.update.spicy-bigint.jpg",
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );

                given(adminProductService.update(eq(1L), any(AdminProductRequest.class)))
                        .willReturn(response);

                // when & then
                mockMvc.perform(put("/admin/product/{id}", 1L)
                                .with(as(MemberRole.ADMIN))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.data.id").value(1))
                        .andExpect(jsonPath("$.data.name").value("수정된 대-정수의 핵불닭맛 감자칩"))
                        .andExpect(jsonPath("$.data.categoryId").value(4))
                        .andExpect(jsonPath("$.data.category").value("신규 과자"))
                        .andExpect(jsonPath("$.data.price").value(2000))
                        .andExpect(jsonPath("$.data.stock").value(20))
                        .andExpect(jsonPath("$.data.status").value("INACTIVE"))
                        .andExpect(jsonPath("$.data.imageUrl").value("https://image.test.update.spicy-bigint.jpg"));
            }
        }
    }

    @Nested
    @DisplayName("Describe: changeStatus() 메서드는")
    class Describe_changeStatus {

        @Nested
        @DisplayName("Context: 올바른 상태 변경 요청인 경우")
        class Context_valid_request {

            @Test
            @DisplayName("It: 상품 상태를 변경")
            void It_상품_상태를_변경() throws Exception {
                // given
                AdminProductStatusUpdateRequest request =
                        new AdminProductStatusUpdateRequest(ProductStatus.INACTIVE);

                AdminProductStatusResponse response = new AdminProductStatusResponse(
                        1L,
                        ProductStatus.INACTIVE,
                        LocalDateTime.now()
                );

                given(adminProductService.changeStatus(eq(1L), any(AdminProductStatusUpdateRequest.class)))
                        .willReturn(response);

                // when & then
                mockMvc.perform(patch("/admin/product/{id}/status", 1L)
                                .with(as(MemberRole.ADMIN))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.data.id").value(1))
                        .andExpect(jsonPath("$.data.status").value("INACTIVE"));
            }
        }
    }

    @Nested
    @DisplayName("Describe: admin API를")
    class Describe_admin_api_authorization {

        @Nested
        @DisplayName("Context: USER 권한으로 요청한 경우")
        class Context_with_user_role {

            @Test
            @DisplayName("It: 403 Forbidden을 반환")
            void It_403을_반환() throws Exception {
                // when & then
                mockMvc.perform(get("/admin/product").with(as(MemberRole.USER)))
                        .andExpect(status().isForbidden());
            }
        }
    }

    private RequestPostProcessor as(MemberRole role) {
        UserDetails principal = new MemberDetails(
                1L,
                role.name().toLowerCase() + "@test.com",
                "password",
                role
        );

        return authentication(
                new UsernamePasswordAuthenticationToken(
                        principal,
                        "token",
                        principal.getAuthorities()
                )
        );
    }

    private AdminProductRequest createRequest(ProductStatus status) {
        return new AdminProductRequest(
                "대-정수의 핵불닭맛 감자칩",
                50000L,
                3L,
                "상품 설명",
                10,
                "https://image.test.spicy-bigint.jpg",
                status
        );
    }

    private AdminProductDetailResponse createDetailResponse(ProductStatus status) {
        return new AdminProductDetailResponse(
                1L,
                "대-정수의 핵불닭맛 감자칩",
                3L,
                "과자",
                50000L,
                "상품 설명",
                10,
                status,
                "https://image.test.spicy-bigint.jpg",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
