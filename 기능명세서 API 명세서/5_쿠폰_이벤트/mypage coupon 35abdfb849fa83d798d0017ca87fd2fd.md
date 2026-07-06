# /mypage/coupon

개발순서: 2차
기능: 쿠폰함 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EC%BF%A0%ED%8F%B0%ED%95%A8%20845bdfb849fa8275b0da81acab416fcf.md)
기능설명: 쿠폰함 조회
도메인: user/coupon
메서드: GET
사용자/관리자: 사용자

### Request

---

#### `Query Params`

status: `ACTIVE` / `USED` / `EXPIRED` (user_coupon.status) — Response 200

```json
{
  "coupons": [
    {
      "user_coupon_id": 78,
      "coupon_id": 1,
      "name": "신규가입 3천원",
      "discount_type": "FIXED",
      "discount_value": 3000,
      "min_order_price": 0,
      "valid_until": "2026-12-31T23:59:59",
      "issue_type": "SIGNIN",
      "status": "ACTIVE",
      "issued_at": "2026-07-01T00:00:00Z",
      "used_at": null
    }
  ]
}
```

- `discount_type`: `FIXED`(정액) / `PERCENT`(정률)
- `issue_type`: `SIGNIN`(회원가입) / `EVENT`(이벤트)
- `status`(user_coupon.status): `ACTIVE`(사용 가능) / `USED`(사용 완료) / `EXPIRED`(만료)