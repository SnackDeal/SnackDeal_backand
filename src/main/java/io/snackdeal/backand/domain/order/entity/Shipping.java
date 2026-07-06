package io.snackdeal.backand.domain.order.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Shipping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private String receiverName;
    private String receiverPhone;
    private String zipcode;
    private String address;
    private String detailAddress;
    private String deliveryRequest;

    private String courier;
    private String trackingNumber;

    @Enumerated(EnumType.STRING)
    private ShippingStatus status;

    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public Shipping(Long orderId, String receiverName, String receiverPhone, String zipcode,
                     String address, String detailAddress, String deliveryRequest) {
        this.orderId = orderId;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.zipcode = zipcode;
        this.address = address;
        this.detailAddress = detailAddress;
        this.deliveryRequest = deliveryRequest;
        this.status = ShippingStatus.READY;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateTracking(String courier, String trackingNumber) {
        this.courier = courier;
        this.trackingNumber = trackingNumber;
        this.status = ShippingStatus.SHIPPING;
        this.shippedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
