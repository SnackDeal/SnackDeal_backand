# /admin/order

개발순서: 1차
기능: 관리자_주문관리 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EA%B4%80%EB%A6%AC%EC%9E%90_%EC%A3%BC%EB%AC%B8%EA%B4%80%EB%A6%AC%2025abdfb849fa82e79b5f013173035bff.md)
기능설명: 주문 리스트
도메인: admin/order
메서드: GET
사용자/관리자: 관리자

### Request

---

#### **`Query Params**`

| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `keyword` | string | N | 주문번호/구매자 검색 |
| `status` | string | N | 주문상태(`PENDING_PAYMENT`/`PAYMENT_COMPLETED`/`PREPARING_SHIPMENT`/`SHIPPED`/`COMPLETED`/`CANCELLED`) |
| `date_from` | date | N | 시작시간 |
| `date_to` | date | N | 마치는시간 |
| `page` | int | N |  |
| `size` | int | N |  |

### Response

---

**`200`** 

```json
{
  "orders": [
    {
      "order_id": 123,
      "order_number": "ORD-20260701-00123",
      "buyer_email": "test@test.com",
      "buyer_name": "홍길동",
      "main_product_name": "허니버터 외 2",
      "final_amount": 23000,
      "status": "PREPARING_SHIPMENT",
      "ordered_at": "2026-07-01T14:32:00Z"
    }
  ],
  "page": 1, "size": 20, "total": 152
}
```