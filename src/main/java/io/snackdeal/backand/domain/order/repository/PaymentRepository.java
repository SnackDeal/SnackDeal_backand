package io.snackdeal.backand.domain.order.repository;

import io.snackdeal.backand.domain.order.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
