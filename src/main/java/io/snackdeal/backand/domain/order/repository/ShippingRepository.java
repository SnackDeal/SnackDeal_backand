package io.snackdeal.backand.domain.order.repository;

import io.snackdeal.backand.domain.order.entity.Shipping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingRepository extends JpaRepository<Shipping, Long> {
}
