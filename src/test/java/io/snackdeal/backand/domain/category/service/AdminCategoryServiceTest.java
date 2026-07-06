package io.snackdeal.backand.domain.category.service;

import io.snackdeal.backand.api.admin.category.dto.CategoryRequest;
import io.snackdeal.backand.api.admin.category.dto.CategoryResponse;
import io.snackdeal.backand.domain.category.entity.Category;
import io.snackdeal.backand.domain.category.repository.CategoryRepository;
import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
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

    @Test
    @DisplayName("findList - 삭제되지 않은 카테고리 목록을 sortOrder ASC, id ASC 순으로 조회한다")
    void findList_success() {
        // given
        Category category1 = createCategory(1L, "과자", 1);
        Category category2 = createCategory(2L, "음료", 2);
        given(categoryRepository.findAllByDeletedAtIsNullOrderBySortOrderAscIdAsc())
                .willReturn(List.of(category1, category2));

        // when
        List<CategoryResponse> result = adminCategoryService.findList();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).name()).isEqualTo("과자");
        assertThat(result.get(0).sortOrder()).isEqualTo(1);
        assertThat(result.get(1).id()).isEqualTo(2L);
        assertThat(result.get(1).name()).isEqualTo("음료");
        assertThat(result.get(1).sortOrder()).isEqualTo(2);
    }

    @Test
    @DisplayName("save - 카테고리 생성 성공")
    void save_success() {
        // given
        CategoryRequest request = new CategoryRequest("과자", 1);
        Category savedCategory = createCategory(1L, "과자", 1);
        given(categoryRepository.existsByName("과자")).willReturn(false);
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
        given(categoryRepository.existsByName("과자")).willReturn(true);

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
        given(categoryRepository.existsByNameAndIdNot("새과자", id)).willReturn(false);

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
        given(categoryRepository.existsByNameAndIdNot("중복이름", id)).willReturn(true);

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
