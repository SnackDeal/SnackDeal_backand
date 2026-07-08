package io.snackdeal.backand.domain.category.repository;

import io.snackdeal.backand.domain.category.entity.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("삭제되지 않은 카테고리만 sortOrder 오름차순, id 오름차순으로 조회한다")
    void findAllByDeletedAtIsNullOrderBySortOrderAscIdAsc() {
        // given
        Category c1 = Category.builder().name("과자").sortOrder(2).build();
        Category c2 = Category.builder().name("음료").sortOrder(1).build();
        categoryRepository.saveAll(List.of(c1, c2));

        // when
        List<Category> result = categoryRepository.findAllByDeletedAtIsNullOrderBySortOrderAscIdAsc();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("음료");
        assertThat(result.get(1).getName()).isEqualTo("과자");
    }

    @Test
    @DisplayName("삭제된 카테고리는 목록에서 제외된다")
    void findAllExcludesDeleted() {
        // given
        Category c1 = Category.builder().name("과자").sortOrder(1).build();
        Category c2 = Category.builder().name("음료").sortOrder(2).build();
        c2.delete();
        categoryRepository.saveAll(List.of(c1, c2));

        // when
        List<Category> result = categoryRepository.findAllByDeletedAtIsNullOrderBySortOrderAscIdAsc();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("과자");
    }

    @Test
    @DisplayName("삭제되지 않은 카테고리를 id로 조회할 수 있다")
    void findByIdAndDeletedAtIsNull() {
        // given
        Category saved = categoryRepository.save(Category.builder().name("과자").sortOrder(1).build());

        // when
        Optional<Category> result = categoryRepository.findByIdAndDeletedAtIsNull(saved.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("과자");
    }

    @Test
    @DisplayName("삭제된 카테고리는 id로 조회되지 않는다")
    void findByIdAndDeletedAtIsNull_ExcludesDeleted() {
        // given
        Category saved = Category.builder().name("과자").sortOrder(1).build();
        saved.delete();
        categoryRepository.save(saved);

        // when
        Optional<Category> result = categoryRepository.findByIdAndDeletedAtIsNull(saved.getId());

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("이름으로 존재 여부를 확인할 수 있다")
    void existsByName() {
        // given
        categoryRepository.save(Category.builder().name("과자").sortOrder(1).build());

        // when & then
        assertThat(categoryRepository.existsByName("과자")).isTrue();
        assertThat(categoryRepository.existsByName("음료")).isFalse();
    }

    @Test
    @DisplayName("특정 id를 제외하고 이름 존재 여부를 확인할 수 있다")
    void existsByNameAndIdNot() {
        // given
        Category saved = categoryRepository.save(Category.builder().name("과자").sortOrder(1).build());

        // when & then
        assertThat(categoryRepository.existsByNameAndIdNot("과자", saved.getId())).isFalse();
        assertThat(categoryRepository.existsByNameAndIdNot("과자", 999L)).isTrue();
    }

    @Test
    @DisplayName("삭제되지 않은 카테고리의 수를 반환한다")
    void countByDeletedAtIsNull() {
        // given
        Category c1 = Category.builder().name("과자").sortOrder(1).build();
        Category c2 = Category.builder().name("음료").sortOrder(2).build();
        c2.delete();
        categoryRepository.saveAll(List.of(c1, c2));

        // when
        long count = categoryRepository.countByDeletedAtIsNull();

        // then
        assertThat(count).isEqualTo(1);
    }
}
