package io.snackdeal.backand.domain.order.repository;

import io.snackdeal.backand.domain.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // 주문에 속한 항목 조회 (상세/목록 대표상품명 계산용).
    List<OrderItem> findByOrderId(Long orderId);
}
