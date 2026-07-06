# /admin/order/{id}/status

개발순서: 1차
기능: 관리자_주문관리 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EA%B4%80%EB%A6%AC%EC%9E%90_%EC%A3%BC%EB%AC%B8%EA%B4%80%EB%A6%AC%2025abdfb849fa82e79b5f013173035bff.md)
기능설명:  주문 상태 변경
도메인: admin/order
메서드: PATCH
사용자/관리자: 관리자

### Request

---

#### `Body`

| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| status | string | Y | 변경할 상태 |
| memo | string | N | 관리자 메모 (선택, 감사 로그용) |

```json
{ "status": "SHIPPED" }
```

**변경 가능한 상태값** (orders.status)

| 값 | 설명 |
| --- | --- |
| `PREPARING_SHIPMENT` | 배송준비중 |
| `SHIPPED` | 배송중 |
| `COMPLETED` | 배송완료 |
| `CANCELLED` | 주문 취소 (배송 전에만 가능, 재고·쿠폰 복구 트리거) |

> `PENDING_PAYMENT` / `PAYMENT_COMPLETED` / `REFUND_REQUESTED` / `REFUND_COMPLETED`는 이 API로 변경 불가
> 
> - `PENDING_PAYMENT`, `PAYMENT_COMPLETED`: 결제 API가 관리
> - `REFUND_REQUESTED`: 사용자 환불요청 API(`/order/{id}/refund`)가 관리
> - `REFUND_COMPLETED`: 관리자 환불처리 API(`/admin/order/{id}/refund`)가 관리

**허용되는 상태 전이**

`PAYMENT_COMPLETED ─→ PREPARING_SHIPMENT ─→ SHIPPED ─→ COMPLETED
        │                    │
        └──────→ CANCELLED ←─┘`

### Response

---

**`200` 상태 변경 성공**

```json
{
  "id": 123,
  "order_number": "ORD-20260701-00123",
  "status": "SHIPPED",
  "manual_override": true,
  "updated_at": "2026-07-01T15:00:00Z"
}
```

이 API로 상태를 변경하면 `manual_override`가 `true`가 되며, **이후 해당 주문은 스케줄러 자동 진행 대상에서 제외**된다.

`400` 허용되지 않는 상태값

```json
{ "error": 
	{ 
	"code": "BAD_REQUEST", 
	"message": "허용되지 않는 상태값입니다" 
	} 
}
```

`422` 잘못된 상태 전이

```json
{ "error": 
	{ 
	"code": "UNPROCESSABLE", 
	"message": "배송완료된 주문은 이전 상태로 되돌릴 수 없습니다" 
	} 
}
```

전이 규칙 위반 사례

- 이미 `COMPLETED`인 주문을 `PREPARING_SHIPMENT`으로 되돌리기
- `CANCELLED` 주문을 다시 `SHIPPED`로 변경
- `SHIPPED` 상태 주문을 `CANCELLED`로 (배송 시작 후 취소는 환불 프로세스로)

**취소(`CANCELLED`) 처리 시**

- 배송 전 상태(`PAYMENT_COMPLETED`/`PREPARING_SHIPMENT`)에서만 가능
- 트랜잭션으로: 상태 변경 + cancelled_at 기록 + 재고 복구 + 사용 쿠폰 복구

```sql
BEGIN;
  UPDATE orders SET status = 'CANCELLED', cancelled_at = NOW() WHERE id = :id;
  UPDATE product SET stock = stock + :qty WHERE id = :product_id;  -- 각 아이템별
  UPDATE user_coupon SET status = 'ACTIVE', used_at = NULL 
    WHERE id = :user_coupon_id;
COMMIT;
```

`403` 관리자 권한 없음 · `404` 존재하지 않는 주문