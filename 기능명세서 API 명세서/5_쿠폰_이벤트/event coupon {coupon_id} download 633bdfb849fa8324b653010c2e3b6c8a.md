# /event/coupon/{coupon_id}/download

개발순서: 2차
기능: 이벤트페이지(쿠폰 다운로드) (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EC%9D%B4%EB%B2%A4%ED%8A%B8%ED%8E%98%EC%9D%B4%EC%A7%80(%EC%BF%A0%ED%8F%B0%20%EB%8B%A4%EC%9A%B4%EB%A1%9C%EB%93%9C)%205cfbdfb849fa826e86a581f002540975.md)
기능설명: 쿠폰 받기
도메인: user/coupon
메서드: POST
사용자/관리자: 사용자

### Response

---

**`201`** 쿠폰함에 지급 성공

`401`미인증 · `409`이미 받음 · `422` 수량 소진/오픈 전