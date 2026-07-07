package io.snackdeal.backand.api.admin.category.controller;

import io.snackdeal.backand.api.admin.category.dto.CategoryOrderRequest;
import io.snackdeal.backand.api.admin.category.dto.CategoryResponse;
import io.snackdeal.backand.domain.category.service.AdminCategoryService;
import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
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

import java.util.List;

import static org.mockito.BDDMockito.*;
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

    @Disabled("TODO: implement")
    @Test
    @DisplayName("list - TODO")
    void list_Success() throws Exception {
        throw new UnsupportedOperationException("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("save - TODO")
    void save_Success() throws Exception {
        throw new UnsupportedOperationException("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("update - TODO")
    void update_Success() throws Exception {
        throw new UnsupportedOperationException("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("delete - TODO")
    void delete_Success() throws Exception {
        throw new UnsupportedOperationException("not implemented");
    }
}
