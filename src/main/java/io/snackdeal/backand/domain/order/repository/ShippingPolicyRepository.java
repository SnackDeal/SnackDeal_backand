package io.snackdeal.backand.domain.order.repository;

import io.snackdeal.backand.domain.order.entity.ShippingPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingPolicyRepository extends JpaRepository<ShippingPolicy, Long> {
}
