# /order/{order_id}/refund

개발순서: 2차
기능: 주문내역 조회 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EC%A3%BC%EB%AC%B8%EB%82%B4%EC%97%AD%20%EC%A1%B0%ED%9A%8C%20734bdfb849fa829ba4a501e1041ce6d6.md)
기능설명: 환불요청
도메인: user/order
메서드: POST
사용자/관리자: 사용자

### Request

---

#### Request Body

| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `reason` | string | Y | 환불 사유 |

### Response

---

**`200`** 환불 요청 접수 → orders.status=`REFUND_REQUESTED` `422` 배송중/완료(SHIPPED/COMPLETED) 상태라 요청 불가

> 사용자 요청 시 orders.status=`REFUND_REQUESTED`로 변경된다. 이후 관리자가 승인하면 `REFUND_COMPLETED`(+ payment.status=`CANCELLED`, cancelled_at 기록), 거절하면 요청 이전 상태로 복귀한다.