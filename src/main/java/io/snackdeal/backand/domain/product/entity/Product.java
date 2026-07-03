package io.snackdeal.backand.domain.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Long price;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    private Integer stock;

    private LocalDateTime deletedAt;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

    private Long categoryId;

    @Builder
    public Product(String name, Long price, String description, Integer stock, Long categoryId) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.status = ProductStatus.ACTIVE;
        this.stock = stock;
        this.categoryId = categoryId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void decreaseStock(int quantity) {
        this.stock -= quantity;
    }

    public void increaseStock(int quantity) {
        this.stock += quantity;
    }

    public void changeStatus(ProductStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
}
