package io.snackdeal.backand.domain.category.repository;

import io.snackdeal.backand.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
