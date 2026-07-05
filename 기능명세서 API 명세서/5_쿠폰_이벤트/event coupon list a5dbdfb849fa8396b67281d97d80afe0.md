# /event/coupon/list

개발순서: 2차
기능: 이벤트페이지(쿠폰 다운로드) (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EC%9D%B4%EB%B2%A4%ED%8A%B8%ED%8E%98%EC%9D%B4%EC%A7%80(%EC%BF%A0%ED%8F%B0%20%EB%8B%A4%EC%9A%B4%EB%A1%9C%EB%93%9C)%205cfbdfb849fa826e86a581f002540975.md)
기능설명: 이벤트 쿠폰 목록
도메인: user/coupon
메서드: GET
사용자/관리자: 사용자

### Request

---

조회는 비회원 가능, "받기"는 로그인 필요

### Response

---

**`200`** 이벤트게시판에 게시된 다운로드 가능 쿠폰 목록

```json
{
  "coupon_board": {
    "id": 3, "title": "여름 이벤트",
    "thumbnail_url": "https://.../summer.jpg",
    "start_at": "2026-07-05T00:00:00", "end_at": "2026-08-31T23:59:59"
  },
  "coupons": [
    {
      "id": 5, "name": "여름맞이 10%",
      "discount_type": "PERCENT", "discount_value": 10,
      "min_order_price": 20000,
      "valid_from": "2026-07-10T00:00:00", "valid_until": "2026-08-31T23:59:59",
      "remaining_quantity": 255,
      "state": "open",
      "already_downloaded": false
    }
  ]
}
```

- state: `upcoming`(게시됐지만 valid_from 전=다운로드 지정일 미도래) / `open`(다운로드 가능) / `soldout`(수량 소진) / `closed`(기간 종료)
- 이벤트게시판(coupon_board)은 valid_from 이전에도 사전 노출되며, 실제 "받기"는 valid_from(지정일)부터 가능