package io.snackdeal.backand.domain.cart.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long memberId;
    private Long productId;

    @Builder
    public CartItem(Integer quantity, Long memberId, Long productId) {
        this.quantity = quantity;
        this.memberId = memberId;
        this.productId = productId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void changeQuantity(int quantity) {
        this.quantity = quantity;
        this.updatedAt = LocalDateTime.now();
    }

    public void addQuantity(int quantity) {
        this.quantity += quantity;
        this.updatedAt = LocalDateTime.now();
    }
}
