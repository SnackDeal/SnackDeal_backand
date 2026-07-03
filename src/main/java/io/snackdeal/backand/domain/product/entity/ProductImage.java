package io.snackdeal.backand.domain.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String attachmentUrl;

    private Integer sortOrder;

    private Long productId;

    @Builder
    public ProductImage(String attachmentUrl, Integer sortOrder, Long productId) {
        this.attachmentUrl = attachmentUrl;
        this.sortOrder = sortOrder;
        this.productId = productId;
    }
}
