package io.snackdeal.backand.domain.product.repository;

import io.snackdeal.backand.domain.product.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
}
