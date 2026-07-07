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

    // 기본 1장 예정. 추후 이미지 api 생성시 수정 가능성
    private Integer sortOrder;

    private Long productId;

    public void updateAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    @Builder
    public ProductImage(String attachmentUrl, Integer sortOrder, Long productId) {
        this.attachmentUrl = attachmentUrl;
        this.sortOrder = sortOrder;
        this.productId = productId;
    }
}
