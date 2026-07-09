package io.snackdeal.backand.domain.category.service;

import io.snackdeal.backand.api.admin.category.dto.CategoryOrderRequest;
import io.snackdeal.backand.api.admin.category.dto.CategoryResponse;
import io.snackdeal.backand.domain.category.entity.Category;
import io.snackdeal.backand.domain.category.repository.CategoryRepository;
import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AdminCategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private AdminCategoryService adminCategoryService;

    @Nested
    @DisplayName("findList")
    class FindList {

        @Test
        @DisplayName("카테고리 목록을 sortOrder 오름차순, id 오름차순으로 정렬하여 반환한다")
        void findList_Success() {
            // given
            Category snack = Category.builder().name("과자").sortOrder(2).build();
            Category drink = Category.builder().name("음료").sortOrder(1).build();
            Category bread = Category.builder().name("빵").sortOrder(1).build();

            // sortOrder 1: drink(id 2), bread(id 3) → id asc
            // sortOrder 2: snack(id 1)
            given(categoryRepository.findAllByOrderBySortOrderAscIdAsc())
                    .willReturn(List.of(drink, bread, snack));

            // when
            List<CategoryResponse> result = adminCategoryService.findList();

            // then
            assertThat(result).hasSize(3);
            assertThat(result.get(0).name()).isEqualTo("음료");
            assertThat(result.get(0).sortOrder()).isEqualTo(1);
            assertThat(result.get(1).name()).isEqualTo("빵");
            assertThat(result.get(1).sortOrder()).isEqualTo(1);
            assertThat(result.get(2).name()).isEqualTo("과자");
            assertThat(result.get(2).sortOrder()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("updateOrder")
    class UpdateOrder {

        @Test
        @DisplayName("유효한 순서 변경 요청이면 각 카테고리의 sortOrder 를 업데이트한다")
        void updateOrder_Success() {
            // given
            Category category1 = Category.builder().name("과자").sortOrder(1).build();
            Category category2 = Category.builder().name("음료").sortOrder(2).build();

            given(categoryRepository.findById(1L)).willReturn(Optional.of(category1));
            given(categoryRepository.findById(2L)).willReturn(Optional.of(category2));

            List<CategoryOrderRequest.CategoryOrderItem> items = List.of(
                    new CategoryOrderRequest.CategoryOrderItem(1L, 2),
                    new CategoryOrderRequest.CategoryOrderItem(2L, 1)
            );
            CategoryOrderRequest request = new CategoryOrderRequest(items);
            given(categoryRepository.count()).willReturn(2L);

            // when
            adminCategoryService.updateOrder(request);

            // then
            assertThat(category1.getSortOrder()).isEqualTo(2);
            assertThat(category2.getSortOrder()).isEqualTo(1);
        }

        @Test
        @DisplayName("존재하지 않는 categoryId 가 포함되면 CATEGORY_NOT_FOUND 예외가 발생한다")
        void updateOrder_NonExistingCategoryId() {
            // given
            given(categoryRepository.findById(999L)).willReturn(Optional.empty());

            List<CategoryOrderRequest.CategoryOrderItem> items = List.of(
                    new CategoryOrderRequest.CategoryOrderItem(999L, 1)
            );
            CategoryOrderRequest request = new CategoryOrderRequest(items);
            given(categoryRepository.count()).willReturn(1L);

            // when & then
            assertThatThrownBy(() -> adminCategoryService.updateOrder(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ResponseCode.CATEGORY_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("전체 카테고리 수와 요청 항목 수가 다르면 CATEGORY_ORDER_SIZE_MISMATCH 예외가 발생한다")
        void updateOrder_SizeMismatch() {
            // given
            List<CategoryOrderRequest.CategoryOrderItem> items = List.of(
                    new CategoryOrderRequest.CategoryOrderItem(1L, 1)
            );
            CategoryOrderRequest request = new CategoryOrderRequest(items);
            given(categoryRepository.count()).willReturn(2L);

            // when & then
            assertThatThrownBy(() -> adminCategoryService.updateOrder(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ResponseCode.CATEGORY_ORDER_SIZE_MISMATCH.getMessage());
        }

        @Test
        @DisplayName("음수 sortOrder 가 포함되면 VALIDATION_FAILED 예외가 발생한다")
        void updateOrder_NegativeSortOrder() {
            // given
            List<CategoryOrderRequest.CategoryOrderItem> items = List.of(
                    new CategoryOrderRequest.CategoryOrderItem(1L, -1)
            );
            CategoryOrderRequest request = new CategoryOrderRequest(items);

            // when & then
            assertThatThrownBy(() -> adminCategoryService.updateOrder(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ResponseCode.VALIDATION_FAILED.getMessage());
        }

        @Test
        @DisplayName("중복된 categoryId 가 포함되면 DUPLICATE_CATEGORY_ORDER_ID 예외가 발생한다")
        void updateOrder_DuplicateCategoryId() {
            // given
            List<CategoryOrderRequest.CategoryOrderItem> items = List.of(
                    new CategoryOrderRequest.CategoryOrderItem(1L, 1),
                    new CategoryOrderRequest.CategoryOrderItem(1L, 2)
            );
            CategoryOrderRequest request = new CategoryOrderRequest(items);

            // when & then
            assertThatThrownBy(() -> adminCategoryService.updateOrder(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ResponseCode.DUPLICATE_CATEGORY_ORDER_ID.getMessage());
        }

        @Test
        @DisplayName("중복된 sortOrder 가 포함되면 DUPLICATE_CATEGORY_SORT_ORDER 예외가 발생한다")
        void updateOrder_DuplicateSortOrder() {
            // given
            List<CategoryOrderRequest.CategoryOrderItem> items = List.of(
                    new CategoryOrderRequest.CategoryOrderItem(1L, 1),
                    new CategoryOrderRequest.CategoryOrderItem(2L, 1)
            );
            CategoryOrderRequest request = new CategoryOrderRequest(items);

            // when & then
            assertThatThrownBy(() -> adminCategoryService.updateOrder(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ResponseCode.DUPLICATE_CATEGORY_SORT_ORDER.getMessage());
        }
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("save - TODO")
    void save_Success() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("update - TODO")
    void update_Success() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("delete - TODO")
    void delete_Success() {
        throw new UnsupportedOperationException("not implemented");
    }
}
