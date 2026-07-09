package io.snackdeal.backand.domain.product.repository;

import io.snackdeal.backand.domain.product.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    Optional<ProductImage> findByProductId(Long productId);
    List<ProductImage> findByProductIdIn(List<Long> productIds);
}
