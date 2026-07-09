package io.snackdeal.backand.domain.product.entity;

import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
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

    private Long recentSalesCount;

    private LocalDateTime deletedAt;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

    private Long categoryId;

    @Builder
    public Product(String name, Long price, String description, Integer stock, Long categoryId, ProductStatus status) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.status = status;
        this.stock = stock;
        this.recentSalesCount=0L;
        this.categoryId = categoryId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.deletedAt=null;
    }

    public void decreaseStock(int quantity) {
        if (this.stock < quantity){
            throw new BusinessException(ResponseCode.OUT_OF_STOCK);
        }
        this.stock -= quantity;
        this.updatedAt=LocalDateTime.now();
    }

    public void increaseStock(int quantity) {
        this.stock += quantity;
        this.updatedAt=LocalDateTime.now();
    }

    public void changeStatus(ProductStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();

        if (status==ProductStatus.DELETED)
            this.deletedAt=LocalDateTime.now();
    }

    public void updateProduct(
            String name,
            Long price,
            String description,
            Integer stock,
            Long categoryId,
            ProductStatus status
    ) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.stock = stock;
        this.categoryId = categoryId;
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isSoldout() {
        return this.stock != null && this.stock == 0;
    }

}
