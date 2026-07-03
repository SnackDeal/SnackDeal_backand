package io.snackdeal.backand.domain.order.repository;

import io.snackdeal.backand.domain.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
