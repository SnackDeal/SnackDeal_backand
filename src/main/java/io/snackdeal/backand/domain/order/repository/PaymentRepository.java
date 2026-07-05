package io.snackdeal.backand.domain.order.repository;

import io.snackdeal.backand.domain.order.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // 주문 1건당 결제 1건 (uk_payment_order).
    Optional<Payment> findByOrderId(Long orderId);
}
