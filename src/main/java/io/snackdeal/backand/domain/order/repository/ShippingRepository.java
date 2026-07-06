package io.snackdeal.backand.domain.order.repository;

import io.snackdeal.backand.domain.order.entity.Shipping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShippingRepository extends JpaRepository<Shipping, Long> {

    // 주문 1건당 배송 1건 (uk_shipping_order).
    Optional<Shipping> findByOrderId(Long orderId);
}
