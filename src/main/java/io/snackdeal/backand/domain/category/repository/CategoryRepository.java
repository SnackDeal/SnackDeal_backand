package io.snackdeal.backand.domain.category.repository;

import io.snackdeal.backand.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c ORDER BY c.sortOrder ASC, c.id ASC")
    List<Category> findAllByOrderBySortOrderAscIdAsc();
}
