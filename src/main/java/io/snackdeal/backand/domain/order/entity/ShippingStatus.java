package io.snackdeal.backand.domain.order.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "배송 상태: READY(배송준비전) / PREPARING(포장중) / SHIPPING(배송중) / DELIVERED(배송완료)")
public enum ShippingStatus {
    READY, PREPARING, SHIPPING, DELIVERED
}
