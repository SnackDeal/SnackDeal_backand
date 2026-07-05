# /order/list

개발순서: 1차
기능: 주문내역 조회 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EC%A3%BC%EB%AC%B8%EB%82%B4%EC%97%AD%20%EC%A1%B0%ED%9A%8C%20734bdfb849fa829ba4a501e1041ce6d6.md)
기능설명: 내 주문내역
도메인: user/order
메서드: GET
사용자/관리자: 사용자

### Request

---

#### `Query Params`

| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `page` | int | N | 페이지네이션 |
| `size` | int | N | 페이지네이션 |

### Response

---

**`200`**

```json
{
  "orders": [
    {
      "order_id": 123,
      "order_number": "ORD-20260701-00123",
      "ordered_at": "2026-07-01T14:32:00Z",
      "main_product_name": "허니버터 프레첼",
      "item_count": 3,
      "final_amount": 12200,
      "status": "PREPARING_SHIPMENT"
    }
  ],
  "page": 1, "size": 10, "total": 7
}
```

주문 상태(orders.status): `PENDING_PAYMENT`(결제대기) / `PAYMENT_COMPLETED`(결제완료) / `PREPARING_SHIPMENT`(배송준비중) / `SHIPPED`(배송중) / `COMPLETED`(배송완료) / `CANCELLED`(취소) / `REFUND_REQUESTED`(환불요청) / `REFUND_COMPLETED`(환불완료)

> ※ 배송 상세 상태는 `shipping.status`(READY/PREPARING/SHIPPING/DELIVERED), 결제 상태는 `payment.status`(READY/PAID/FAILED/CANCELLED)로 별도 관리. 환불 흐름: 사용자 요청 시 `REFUND_REQUESTED` → 관리자 승인 시 `REFUND_COMPLETED`(+ payment.status=`CANCELLED`)