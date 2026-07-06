package io.snackdeal.backand.domain.category.repository;

import io.snackdeal.backand.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByDeletedAtIsNullOrderBySortOrderAscIdAsc();

    Optional<Category> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}
