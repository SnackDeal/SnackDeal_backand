package io.snackdeal.backand.domain.delivery.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String receiverName;
    private String receiverPhone;
    private String zipcode;
    private String address;
    private String detailAddress;

    private boolean isDefault;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    private Long memberId;

    @Builder
    public Delivery(String name, String receiverName, String receiverPhone, String zipcode,
                     String address, String detailAddress, boolean isDefault, Long memberId) {
        this.name = name;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.zipcode = zipcode;
        this.address = address;
        this.detailAddress = detailAddress;
        this.isDefault = isDefault;
        this.memberId = memberId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsDefault() {
        this.isDefault = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void unmarkDefault() {
        this.isDefault = false;
        this.updatedAt = LocalDateTime.now();
    }
}
