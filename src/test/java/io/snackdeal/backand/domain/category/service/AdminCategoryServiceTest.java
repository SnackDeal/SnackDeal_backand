package io.snackdeal.backand.domain.category.service;

import io.snackdeal.backand.api.admin.category.dto.CategoryOrderRequest;
import io.snackdeal.backand.api.admin.category.dto.CategoryRequest;
import io.snackdeal.backand.api.admin.category.dto.CategoryResponse;
import io.snackdeal.backand.domain.category.entity.Category;
import io.snackdeal.backand.domain.category.repository.CategoryRepository;
import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminCategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private AdminCategoryService adminCategoryService;

    private Category createCategory(Long id, String name, Integer sortOrder) {
        Category category = Category.builder()
                .name(name)
                .sortOrder(sortOrder)
                .build();
        ReflectionTestUtils.setField(category, "id", id);
        return category;
    }

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

            given(categoryRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(category1));
            given(categoryRepository.findByIdAndDeletedAtIsNull(2L)).willReturn(Optional.of(category2));

            List<CategoryOrderRequest.CategoryOrderItem> items = List.of(
                    new CategoryOrderRequest.CategoryOrderItem(1L, 2),
                    new CategoryOrderRequest.CategoryOrderItem(2L, 1)
            );
            CategoryOrderRequest request = new CategoryOrderRequest(items);
            given(categoryRepository.countByDeletedAtIsNull()).willReturn(2L);

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
            given(categoryRepository.findByIdAndDeletedAtIsNull(999L)).willReturn(Optional.empty());

            List<CategoryOrderRequest.CategoryOrderItem> items = List.of(
                    new CategoryOrderRequest.CategoryOrderItem(999L, 1)
            );
            CategoryOrderRequest request = new CategoryOrderRequest(items);
            given(categoryRepository.countByDeletedAtIsNull()).willReturn(1L);

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
            given(categoryRepository.countByDeletedAtIsNull()).willReturn(2L);

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

    @Test
    @DisplayName("save - 카테고리 생성 성공")
    void save_success() {
        // given
        CategoryRequest request = new CategoryRequest("과자", 1);
        Category savedCategory = createCategory(1L, "과자", 1);
        given(categoryRepository.existsByNameAndDeletedAtIsNull("과자")).willReturn(false);
        given(categoryRepository.save(any(Category.class))).willReturn(savedCategory);

        // when
        CategoryResponse result = adminCategoryService.save(request);

        // then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("과자");
        assertThat(result.sortOrder()).isEqualTo(1);
    }

    @Test
    @DisplayName("save - 중복된 이름이면 DUPLICATE_CATEGORY 예외 발생")
    void save_duplicateName_fail() {
        // given
        CategoryRequest request = new CategoryRequest("과자", 1);
        given(categoryRepository.existsByNameAndDeletedAtIsNull("과자")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> adminCategoryService.save(request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("responseCode", ResponseCode.DUPLICATE_CATEGORY);

        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("update - 카테고리 수정 성공")
    void update_success() {
        // given
        Long id = 1L;
        CategoryRequest request = new CategoryRequest("새과자", 2);
        Category category = createCategory(id, "과자", 1);
        given(categoryRepository.findByIdAndDeletedAtIsNull(id)).willReturn(Optional.of(category));
        given(categoryRepository.existsByNameAndIdNotAndDeletedAtIsNull("새과자", id)).willReturn(false);

        // when
        CategoryResponse result = adminCategoryService.update(id, request);

        // then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("새과자");
        assertThat(result.sortOrder()).isEqualTo(2);
    }

    @Test
    @DisplayName("update - 존재하지 않는 카테고리면 CATEGORY_NOT_FOUND 예외 발생")
    void update_notFound_fail() {
        // given
        Long id = 999L;
        CategoryRequest request = new CategoryRequest("과자", 1);
        given(categoryRepository.findByIdAndDeletedAtIsNull(id)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminCategoryService.update(id, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("responseCode", ResponseCode.CATEGORY_NOT_FOUND);
    }

    @Test
    @DisplayName("update - 다른 카테고리와 이름이 중복되면 DUPLICATE_CATEGORY 예외 발생")
    void update_duplicateName_fail() {
        // given
        Long id = 1L;
        CategoryRequest request = new CategoryRequest("중복이름", 1);
        Category category = createCategory(id, "과자", 1);
        given(categoryRepository.findByIdAndDeletedAtIsNull(id)).willReturn(Optional.of(category));
        given(categoryRepository.existsByNameAndIdNotAndDeletedAtIsNull("중복이름", id)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> adminCategoryService.update(id, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("responseCode", ResponseCode.DUPLICATE_CATEGORY);
    }

    @Test
    @DisplayName("delete - 카테고리 소프트 삭제 성공")
    void delete_success() {
        // given
        Long id = 1L;
        Category category = createCategory(id, "과자", 1);
        given(categoryRepository.findByIdAndDeletedAtIsNull(id)).willReturn(Optional.of(category));

        // when
        adminCategoryService.delete(id);

        // then
        assertThat(category.getDeletedAt()).isNotNull();
        assertThat(category.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("delete - 존재하지 않는 카테고리면 CATEGORY_NOT_FOUND 예외 발생")
    void delete_notFound_fail() {
        // given
        Long id = 999L;
        given(categoryRepository.findByIdAndDeletedAtIsNull(id)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminCategoryService.delete(id))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("responseCode", ResponseCode.CATEGORY_NOT_FOUND);
    }
}