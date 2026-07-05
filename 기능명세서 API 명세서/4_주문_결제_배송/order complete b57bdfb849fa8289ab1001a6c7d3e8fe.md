# /order/complete

개발순서: 1차
기능: 주문정보입력 페이지(결제) (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EC%A3%BC%EB%AC%B8%EC%A0%95%EB%B3%B4%EC%9E%85%EB%A0%A5%20%ED%8E%98%EC%9D%B4%EC%A7%80(%EA%B2%B0%EC%A0%9C)%20227bdfb849fa8273a42d81be9ac71708.md)
기능설명: 결제 검증 및 주문 확정
도메인: user/order
메서드: POST
사용자/관리자: 사용자

프론트가 포트원에서 받은 `imp_uid`를 전달 → 서버가 포트원 API로 실제 결제 금액을 조회해 검증.

[서버 검증 로직]

```
1. imp_uid로 포트원 결제 조회 (GET https://api.iamport.kr/payments/{imp_uid})
2. 포트원의 실제 결제 금액(paid_amount)과 DB의 주문 예정 금액(amount) 비교
3. 일치 → 트랜잭션: 재고 조건부 UPDATE 차감 + user_coupon 사용 처리(status=USED) + orders.status=PAYMENT_COMPLETED + payment.status=PAID + shipping(READY) 생성
4. 불일치 → 포트원 결제취소 API 호출 + payment.status=CANCELLED + 주문 실패
```

### Request

---

#### `Request Body`

| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `imp_uid` | string | Y | 포트원 결제 고유번호  |
| `merchant_uid` | string | Y | prepare에서 발급한 주문번호 |

```json
{
	"imp_uid": "imp_1234567890", 
	"merchant_uid": "ORD-20260701-00123"
}
```

### Response

---

**`200`** 검증 성공 → 주문 확정

```json
{
  "order_id": 123,
  "order_number": "ORD-20260701-00123",
  "status": "PAYMENT_COMPLETED",
  "product_amount": 15200,
  "shipping_fee": 0,
  "discount_amount": 3000,
  "final_amount": 12200,
  "payment": {
    "imp_uid": "imp_1234567890",
    "pay_method": "CARD",
    "pg_provider": "tosspayments",
    "status": "PAID",
    "receipt_url": "https://.../receipt"
  },
  "paid_at": "2026-07-01T14:32:00Z"
}
```

**`422` 금액 불일치(위변조) → 결제 자동 취소 + 주문 실패**

```json
{
	"error": {
		"code": "UNPROCESSABLE"
		"message": "결제 금액이 일치하지 않아 취소되었습니다"
	}
}
```