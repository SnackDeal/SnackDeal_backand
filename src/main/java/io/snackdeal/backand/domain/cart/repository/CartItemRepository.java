package io.snackdeal.backand.domain.cart.repository;

import io.snackdeal.backand.domain.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
