package io.snackdeal.backand.domain.delivery.repository;

import io.snackdeal.backand.domain.delivery.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}
