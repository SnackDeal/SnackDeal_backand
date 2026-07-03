package io.snackdeal.backand.domain.order.repository;

import io.snackdeal.backand.domain.order.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
}
