# /order/{order_id}

개발순서: 1차
기능: 주문내역 조회 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EC%A3%BC%EB%AC%B8%EB%82%B4%EC%97%AD%20%EC%A1%B0%ED%9A%8C%20734bdfb849fa829ba4a501e1041ce6d6.md)
기능설명: 주문 상세
도메인: user/order
메서드: GET
사용자/관리자: 사용자

### Request

---

#### `Path Params`

| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `order_id` | int | Y | 주문 ID (orders.id) |

### Response

---

**`200`**

```json
{
  "order_id": 123,
  "order_number": "ORD-20260701-00123",
  "ordered_at": "2026-07-01T14:32:00Z",
  "status": "PREPARING_SHIPMENT",
  "items": [
    {
      "product_id": 1, "product_name": "허니버터 프레첼",
      "quantity": 2, "price": 4500, "line_total": 9000
    }
  ],
  "shipping": {
    "receiver_name": "홍길동", "receiver_phone": "01012345678",
    "zipcode": "06133", "address": "서울 강남구 테헤란로 123",
    "detail_address": "456호", "delivery_request": "부재 시 문 앞",
    "courier": "CJ대한통운", "tracking_number": "123456789012",
    "status": "PREPARING"
  },
  "payment": {
    "product_amount": 15200, "shipping_fee": 3000,
    "coupon_name": "신규가입 3천원", "discount_amount": 3000,
    "final_amount": 12200, "pay_method": "CARD", "pg_provider": "tosspayments",
    "status": "PAID", "receipt_url": "https://.../receipt",
    "paid_at": "2026-07-01T14:32:00Z"
  }
}
```

`403` 타인 주문 접근 차단 · `404` 없음