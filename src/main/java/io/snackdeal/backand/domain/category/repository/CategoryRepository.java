package io.snackdeal.backand.domain.category.repository;

import io.snackdeal.backand.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE c.deletedAt IS NULL ORDER BY c.sortOrder ASC, c.id ASC")
    List<Category> findAllByOrderBySortOrderAscIdAsc();

    Optional<Category> findByIdAndDeletedAtIsNull(Long id);

    long countByDeletedAtIsNull();

    boolean existsByNameAndDeletedAtIsNull(String name);

    boolean existsByNameAndIdNotAndDeletedAtIsNull(String name, Long id);

}
