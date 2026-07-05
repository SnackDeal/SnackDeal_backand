# /admin/order/{id}/refund

개발순서: 2차
기능: 관리자_주문관리 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EA%B4%80%EB%A6%AC%EC%9E%90_%EC%A3%BC%EB%AC%B8%EA%B4%80%EB%A6%AC%2025abdfb849fa82e79b5f013173035bff.md)
기능설명:  환불 처리
도메인: admin/order
메서드: POST
사용자/관리자: 관리자

### Request

`REFUND_REQUESTED` 상태의 주문을 관리자가 **승인 또는 거절**한다. 실제 PG(포트원) 환불 API는 호출하지 않고 **DB 상태 변경 + 재고 복구**로 처리 (테스트 결제라 실제 금전 이동이 없음).

> **승인 시 orders.status=`REFUND_COMPLETED`(cancelled_at 기록) + payment.status=`CANCELLED`**. 거절 시 요청 이전 상태로 복귀.

---

#### `Body`

| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| approve  | boolean | Y | `true` = 승인 / `false` = 거절 |
| reject_reason | string | 조건부 | `approve=false`일 때 필수 (거절 사유) |
| restore_stock | boolean | N | 재고 복구 여부 (기본 `true`) |

**승인 예시**

```json
{ "approve": true }
```

**거절 예시**

```json
{
  "approve": false,
  "reject_reason": "이미 배송이 시작되어 환불이 불가합니다. 반품 절차로 진행해주세요."
}
```

## **Response**

---

`200` 승인 성공

```json
{
  "id": 123,
  "order_number": "ORD-20260701-00123",
  "status": "REFUND_COMPLETED",
  "cancelled_at": "2026-07-02T10:00:00Z",
  "payment_status": "CANCELLED",
  "stock_restored": true,
  "coupon_restored": false
}
```

`200` 거절 성공 → 상태를 환불 요청 이전 상태(예: `preparing`)로 되돌림

```json
{
  "id": 123,
  "order_number": "ORD-20260701-00123",
  "status": "PREPARING_SHIPMENT",
  "refund_rejected_at": "2026-07-02T10:00:00Z",
  "reject_reason": "이미 배송이 시작되어 환불이 불가합니다..."
}
```

`400` 필수값 누락

```json
{ "error": { "code": "BAD_REQUEST", 
"message": "거절 시 사유는 필수입니다" } }
```

`422` 처리 제한

```json
{ "error": { "code": "UNPROCESSABLE", "message": "환불 요청 상태가 아닙니다" } }
```

**주요 예외 케이스**

- 이미 `REFUND_COMPLETED`(환불 완료) 상태인 주문 → 재환불 시도 차단
- `COMPLETED`(배송완료) 이후 상태 → 이 API로는 처리 불가 (실무에선 반품 프로세스, MVP는 관리자 안내로 대체)
- `REFUND_REQUESTED` 상태가 아닌 주문에 승인 시도

`403` 관리자 권한 없음 · `404` 주문 없음

---

### 승인 시 서버 로직 (트랜잭션)

**PG 환불 없이 DB 상태 변경 + 재고 복구 방식**

```sql
BEGIN TRANSACTION
  1. 주문 상태 검증 (status = 'REFUND_REQUESTED')
     → 아니면 UNPROCESSABLE 반환하고 롤백
  
  2. 주문 상태 변경
     UPDATE orders 
     SET status = 'REFUND_COMPLETED', 
         cancelled_at = NOW()
     WHERE id = :order_id;
     UPDATE payment SET status = 'CANCELLED', cancelled_at = NOW()
     WHERE order_id = :order_id;
  
  3. 재고 복구 (restore_stock = true인 경우)
     각 order_item에 대해:
     UPDATE product 
     SET stock = stock + :quantity
     WHERE id = :product_id;
  
  4. 사용 쿠폰 복구 (선택 정책, MVP는 복구 안 함)
     UPDATE user_coupon
     SET status = 'ACTIVE', used_at = NULL
     WHERE id = :user_coupon_id;
  
  5. 환불 이력 기록 (선택)
COMMIT
```

**만약 실서비스라면 여기에 추가로**

- 포트원 결제취소 API 호출 (`POST /payments/cancel`)
- 실패 시 전체 롤백 + 관리자에게 알림