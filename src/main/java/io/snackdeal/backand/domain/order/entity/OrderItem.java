package io.snackdeal.backand.domain.order.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;
    private Long price;
    private Integer quantity;

    private Long productId;
    private Long orderId;

    @Builder
    public OrderItem(String productName, Long price, Integer quantity, Long productId, Long orderId) {
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.productId = productId;
        this.orderId = orderId;
    }
}
