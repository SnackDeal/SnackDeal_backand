package io.snackdeal.backand.api.admin.category.controller;

import io.snackdeal.backand.api.admin.category.dto.CategoryOrderRequest;
import io.snackdeal.backand.api.admin.category.dto.CategoryRequest;
import io.snackdeal.backand.api.admin.category.dto.CategoryResponse;
import io.snackdeal.backand.api.user.member.dto.MemberDetails;
import io.snackdeal.backand.domain.category.service.AdminCategoryService;
import io.snackdeal.backand.domain.member.entity.MemberRole;
import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.as;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:test-config.properties")
@WithMockUser(roles = "ADMIN")
class AdminCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AdminCategoryService adminCategoryService;

    private RequestPostProcessor as(MemberRole role) {
        UserDetails principal = new MemberDetails(1L, role.name().toLowerCase() + "@test.com", "password", role);
        return authentication(new UsernamePasswordAuthenticationToken(principal, "token", principal.getAuthorities()));
    }

    @Nested
    @DisplayName("PATCH /admin/category/order")
    class UpdateOrder {

        @Test
        @DisplayName("유효한 순서 변경 요청이면 200 OK 를 반환한다")
        void updateOrder_Success() throws Exception {
            // given
            List<CategoryOrderRequest.CategoryOrderItem> items = List.of(
                    new CategoryOrderRequest.CategoryOrderItem(1L, 1),
                    new CategoryOrderRequest.CategoryOrderItem(2L, 2)
            );
            CategoryOrderRequest request = new CategoryOrderRequest(items);
            willDoNothing().given(adminCategoryService).updateOrder(any());

            // when & then
            mockMvc.perform(patch("/admin/category/order")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS.getCode()));
        }

        @Test
        @DisplayName("빈 categoryOrders 목록이면 400 Bad Request 를 반환한다")
        void updateOrder_EmptyList() throws Exception {
            // given
            CategoryOrderRequest request = new CategoryOrderRequest(List.of());

            // when & then
            mockMvc.perform(patch("/admin/category/order")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("존재하지 않는 categoryId 가 포함되면 404 Not Found 를 반환한다")
        void updateOrder_NonExistingCategoryId() throws Exception {
            // given
            List<CategoryOrderRequest.CategoryOrderItem> items = List.of(
                    new CategoryOrderRequest.CategoryOrderItem(999L, 1)
            );
            CategoryOrderRequest request = new CategoryOrderRequest(items);

            willThrow(new BusinessException(ResponseCode.CATEGORY_NOT_FOUND))
                    .given(adminCategoryService).updateOrder(any());

            // when & then
            mockMvc.perform(patch("/admin/category/order")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(ResponseCode.CATEGORY_NOT_FOUND.getCode()));
        }

        @Test
        @DisplayName("전체 카테고리 수와 요청 항목 수가 다르면 400 Bad Request 를 반환한다")
        void updateOrder_SizeMismatch() throws Exception {
            // given
            List<CategoryOrderRequest.CategoryOrderItem> items = List.of(
                    new CategoryOrderRequest.CategoryOrderItem(1L, 1)
            );
            CategoryOrderRequest request = new CategoryOrderRequest(items);

            willThrow(new BusinessException(ResponseCode.CATEGORY_ORDER_SIZE_MISMATCH))
                    .given(adminCategoryService).updateOrder(any());

            // when & then
            mockMvc.perform(patch("/admin/category/order")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(ResponseCode.CATEGORY_ORDER_SIZE_MISMATCH.getCode()));
        }

        @Test
        @DisplayName("음수 sortOrder 가 포함되면 400 Bad Request 를 반환한다")
        void updateOrder_NegativeSortOrder() throws Exception {
            // given
            List<CategoryOrderRequest.CategoryOrderItem> items = List.of(
                    new CategoryOrderRequest.CategoryOrderItem(1L, -1)
            );
            CategoryOrderRequest request = new CategoryOrderRequest(items);

            // when & then
            mockMvc.perform(patch("/admin/category/order")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("중복된 categoryId 가 포함되면 400 Bad Request 를 반환한다")
        void updateOrder_DuplicateCategoryId() throws Exception {
            // given
            List<CategoryOrderRequest.CategoryOrderItem> items = List.of(
                    new CategoryOrderRequest.CategoryOrderItem(1L, 1),
                    new CategoryOrderRequest.CategoryOrderItem(1L, 2)
            );
            CategoryOrderRequest request = new CategoryOrderRequest(items);

            willThrow(new BusinessException(ResponseCode.DUPLICATE_CATEGORY_ORDER_ID))
                    .given(adminCategoryService).updateOrder(any());

            // when & then
            mockMvc.perform(patch("/admin/category/order")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(ResponseCode.DUPLICATE_CATEGORY_ORDER_ID.getCode()));
        }

        @Test
        @DisplayName("중복된 sortOrder 가 포함되면 400 Bad Request 를 반환한다")
        void updateOrder_DuplicateSortOrder() throws Exception {
            // given
            List<CategoryOrderRequest.CategoryOrderItem> items = List.of(
                    new CategoryOrderRequest.CategoryOrderItem(1L, 1),
                    new CategoryOrderRequest.CategoryOrderItem(2L, 1)
            );
            CategoryOrderRequest request = new CategoryOrderRequest(items);

            willThrow(new BusinessException(ResponseCode.DUPLICATE_CATEGORY_SORT_ORDER))
                    .given(adminCategoryService).updateOrder(any());

            // when & then
            mockMvc.perform(patch("/admin/category/order")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(ResponseCode.DUPLICATE_CATEGORY_SORT_ORDER.getCode()));
        }
    }

    @Test
    @DisplayName("list - 관리자 카테고리 목록 조회 성공")
    void list_success() throws Exception {
        // given
        CategoryResponse response = new CategoryResponse(1L, "과자", 1, LocalDateTime.now(), null);
        given(adminCategoryService.findList()).willReturn(List.of(response));

        // when / then
        mockMvc.perform(get("/admin/category").with(as(MemberRole.ADMIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("과자"))
                .andExpect(jsonPath("$.data[0].sortOrder").value(1));
    }

    @Test
    @DisplayName("save - 카테고리 생성 성공")
    void save_success() throws Exception {
        // given
        CategoryRequest request = new CategoryRequest("과자", 1);
        CategoryResponse response = new CategoryResponse(1L, "과자", 1, LocalDateTime.now(), null);
        given(adminCategoryService.save(any(CategoryRequest.class))).willReturn(response);

        // when / then
        mockMvc.perform(post("/admin/category")
                        .with(as(MemberRole.ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("과자"));
    }

    @Test
    @DisplayName("save - name이 빈 문자열이면 400 Bad Request")
    void save_validation_fail_whenNameBlank() throws Exception {
        // given
        CategoryRequest request = new CategoryRequest("", 1);

        // when / then
        mockMvc.perform(post("/admin/category")
                        .with(as(MemberRole.ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(adminCategoryService);
    }

    @Test
    @DisplayName("update - 카테고리 수정 성공")
    void update_success() throws Exception {
        // given
        Long id = 1L;
        CategoryRequest request = new CategoryRequest("새과자", 2);
        CategoryResponse response = new CategoryResponse(1L, "새과자", 2, LocalDateTime.now(), null);
        given(adminCategoryService.update(eq(id), any(CategoryRequest.class))).willReturn(response);

        // when / then
        mockMvc.perform(put("/admin/category/{id}", id)
                        .with(as(MemberRole.ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.sortOrder").value(2));
    }

    @Test
    @DisplayName("update - sortOrder가 음수면 400 Bad Request")
    void update_validation_fail_whenSortOrderNegative() throws Exception {
        // given
        Long id = 1L;
        CategoryRequest request = new CategoryRequest("과자", -1);

        // when & then
        mockMvc.perform(put("/admin/category/{id}", id)
                        .with(as(MemberRole.ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(adminCategoryService);
    }

    @Test
    @DisplayName("delete - 카테고리 삭제 성공")
    void delete_success() throws Exception {
        // given
        Long id = 1L;

        // when / then
        mockMvc.perform(delete("/admin/category/{id}", id).with(as(MemberRole.ADMIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(adminCategoryService).delete(id);
    }

    @Test
    @DisplayName("USER 권한으로 /admin/category 접근 시 403 Forbidden")
    void request_withoutAdminRole_forbidden() throws Exception {
        // when & then
        mockMvc.perform(get("/admin/category").with(as(MemberRole.USER)))
                .andExpect(status().isForbidden());
    }
}
