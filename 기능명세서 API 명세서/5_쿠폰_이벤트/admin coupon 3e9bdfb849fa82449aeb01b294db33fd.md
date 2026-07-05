# /admin/coupon

개발순서: 2차
기능: 관리자_쿠폰관리페이지 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EA%B4%80%EB%A6%AC%EC%9E%90_%EC%BF%A0%ED%8F%B0%EA%B4%80%EB%A6%AC%ED%8E%98%EC%9D%B4%EC%A7%80%2088cbdfb849fa835cb23001d2fd345068.md)
기능설명: 쿠폰 리스트
도메인: admin/coupon
메서드: GET
사용자/관리자: 관리자

### Request

---

#### **`Query Params`**

| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `keyword` | string | N | 쿠폰명 검색 |
| `issue_type` | string | N | `EVENT` / `SIGNIN` |
| `status` | string | N | `ACTIVE` / `EXPIRED` / `STOPPED` (is_active + 유효기간 파생) |
| `page` | int | N | 페이지 (기본 1) |
| `size` | int | N | 페이지당 개수 (기본 20) |

## **Response**

---

`200`

```json
{
  "coupons": [
    {
      "id": 5,
      "name": "여름맞이 10% 할인",
      "discount_type": "PERCENT",
      "discount_value": 10,
      "min_order_price": 20000,
      "valid_from": "2026-07-10",
      "valid_until": "2026-08-31",
      "issue_type": "EVENT",
      "coupon_board_id": 3,
      "coupon_board_title": "여름 이벤트",
      "total_quantity": 300,
      "issued_quantity": 45,
      "used_count": 12,
      "is_active": true,
      "status": "ACTIVE"
    },
    {
      "id": 1,
      "name": "신규가입 3천원",
      "discount_type": "FIXED",
      "discount_value": 3000,
      "min_order_price": 0,
      "valid_from": "2026-01-01",
      "valid_until": "2026-12-31",
      "issue_type": "SIGNIN",
      "coupon_board_id": null,
      "coupon_board_title": null,
      "total_quantity": null,
      "issued_quantity": 128,
      "used_count": 94,
      "is_active": true,
      "status": "ACTIVE"
    }
  ],
  "page": 1,
  "size": 20,
  "total": 12
}
```

**필드 설명**

- `total_quantity`: `null`이면 무제한 발급
- `issued_quantity`: 실제 발급된 수량 (이벤트 쿠폰의 경우 사용자 다운로드 수)
- `used_count`: 사용된 수량 (주문에 적용된 수, user_coupon.status=USED 집계)
- `is_active`: 관리자 강제 중지 플래그(t/f)
- `status`: `is_active`와 유효기간에서 파생 — `ACTIVE`(is_active=true & 유효기간 내) / `EXPIRED`(valid_until 지남) / `STOPPED`(is_active=false)
- `coupon_board_id`: 이 쿠폰이 게시된 이벤트게시판(coupon_board) ID

---