# /admin/order/{id}

개발순서: 1차
기능: 관리자_주문관리 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EA%B4%80%EB%A6%AC%EC%9E%90_%EC%A3%BC%EB%AC%B8%EA%B4%80%EB%A6%AC%2025abdfb849fa82e79b5f013173035bff.md)
기능설명: 주문 상세
도메인: admin/order
메서드: GET
사용자/관리자: 관리자

### Reques

---

관리자가 특정 주문의 전체 정보를 조회. 사용자용 주문 상세와 달리 **관리자에게 필요한 정보(스케줄러 상태, imp_uid, 사용 쿠폰 정보 등)**를 함께 반환.

**`Path Params`**

| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| id | long | Y | 주문 PK (order_number 아님)  |

### Response

---

**`200`** 

```json
{
  "id": 123,
  "order_number": "ORD-20260701-00123",
  "status": "PREPARING_SHIPMENT",
  "ordered_at": "2026-07-01T14:32:00Z",
  "paid_at": "2026-07-01T14:32:15Z",
  "cancelled_at": null,
  "scheduled_next_status": "SHIPPED",
  "scheduled_next_at": "2026-07-01T14:35:00Z",
  "manual_override": false,
  "buyer": {
    "id": 45,
    "email": "hong@test.com",
    "name": "홍길동",
    "total_order_count": 7
  },
  "items": [
    {
      "product_id": 12,
      "product_name": "허니버터 프레첼",
      "price": 4500,
      "quantity": 2,
      "line_total": 9000
    },
    {
      "product_id": 15,
      "product_name": "민트초코 웨하스",
      "price": 3200,
      "quantity": 1,
      "line_total": 3200
    }
  ],
  "shipping": {
    "receiver_name": "홍길동",
    "receiver_phone": "01012345678",
    "zipcode": "06133",
    "address": "서울 강남구 테헤란로 123",
    "detail_address": "456호",
    "delivery_request": "부재 시 문 앞",
    "courier": "CJ대한통운",
    "tracking_number": "123456789012",
    "status": "PREPARING"
  },
  "payment": {
    "product_amount": 12200,
    "shipping_fee": 3000,
    "used_coupon": {
      "user_coupon_id": 78,
      "coupon_name": "신규가입 3천원",
      "discount_type": "FIXED",
      "discount_value": 3000
    },
    "discount_amount": 3000,
    "final_amount": 12200,
    "pay_method": "CARD",
    "pg_provider": "tosspayments",
    "status": "PAID",
    "imp_uid": "imp_1234567890"
  }
}
```

**필드 설명**

- `scheduled_next_status` / `scheduled_next_at`: 스케줄러가 다음에 자동으로 넘길 상태와 시각. 스케줄러 대상이 아니면 `null`
- `manual_override`: 관리자가 수동으로 상태를 변경했는지 여부. `true`면 스케줄러가 이 주문을 건드리지 않음
- `used_coupon`: 사용 쿠폰이 없으면 `null`
- `imp_uid`: 포트원 결제 고유번호 (검증·환불 로그 추적용)
- `buyer.total_order_count`: 이 회원의 누적 주문 수 (관리자가 VIP/신규 여부 판단용, 선택)

`404` 존재하지 않는 주문

```json
{
	"error": {
		"code": "NOT_FOUND"
		"message": "주문을 찾을 수 없습니다"
	}
}
```

`403` 관리자 권한 없음