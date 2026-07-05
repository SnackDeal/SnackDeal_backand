# /order/prepare

개발순서: 1차
기능: 주문정보입력 페이지(결제) (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EC%A3%BC%EB%AC%B8%EC%A0%95%EB%B3%B4%EC%9E%85%EB%A0%A5%20%ED%8E%98%EC%9D%B4%EC%A7%80(%EA%B2%B0%EC%A0%9C)%20227bdfb849fa8273a42d81be9ac71708.md)
기능설명: 결제 준비(포트원api에따라 달라짐)
도메인: user/order
메서드: POST
사용자/관리자: 사용자

포트원 SDK로 토스페이먼츠 결제창을 띄우고, 서버가 실제 결제 금액을 재검증한 뒤 주문을 확정한다. 흐름: `prepare`(주문 임시생성) → 프론트 SDK 결제 → `complete`(서버 검증 + 확정)

### Request

주문을 pending으로 임시 생성하고 검증용 merchant_uid와 결제 예정 금액을 확정한다. 결제창 띄우기 전에 재고를 미리 체크(차감은 아직 안 함).

---

#### `Request Body`

| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| items | array | Y | 주문 상품 목록  |
| items[].product_id | int | Y | 상품ID (product.id) |
| items[].quantity | int | Y | 수량 |
| delivery_id | int | N | 주소록에서 선택한 배송지 ID (delivery.id). 선택 시 아래 shipping 값 자동 채움 |
| shipping | object | Y | 배송지 정보 (delivery_id 미사용 시 직접 입력) |
| shipping.receiver_name | string | Y | 수령인 |
| shipping.receiver_phone | string | Y | 연락처 |
| shipping.zipcode | string | Y | 우편번호 |
| shipping.address | string | Y | 기본 주소 |
| shipping.detail_address | string | N | 상세 주소 |
| shipping.delivery_request | string | N | 배송 요청사항 |
| user_coupon_id | int | N | 사용할 쿠폰 ID (user_coupon.id) |

```json
{
  "items": [ { "product_id": 1, "quantity": 2 } ],
  "delivery_id": 5,
  "shipping": {
    "receiver_name": "홍길동", "receiver_phone": "01012345678",
    "zipcode": "06133", "address": "서울 강남구 테헤란로 123",
    "detail_address": "456호", "delivery_request": "부재 시 문 앞"
  },
  "user_coupon_id": 12
}
```

> 과자 쇼핑몰은 옵션이 없으므로 option_id 없음. 배송지는 [주소록(delivery)]에서 선택하거나 직접 입력한다.

### Response

---

**`201`** 

```json
{
  "merchant_uid": "ORD-20260701-00123",
  "amount": 12200,
  "buyer_email": "test@test.com",
  "buyer_name": "홍길동",
  "buyer_tel": "010-1234-5678"
}
```

**`4**22` 재고 부족 (결제창 진입 전 차단) · `409` 쿠폰 조건 미달/만료