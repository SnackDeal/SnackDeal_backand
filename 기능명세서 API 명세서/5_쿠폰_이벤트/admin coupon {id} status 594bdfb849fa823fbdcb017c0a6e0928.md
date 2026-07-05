# /admin/coupon/{id}/status

개발순서: 2차
기능: 관리자_쿠폰관리페이지 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EA%B4%80%EB%A6%AC%EC%9E%90_%EC%BF%A0%ED%8F%B0%EA%B4%80%EB%A6%AC%ED%8E%98%EC%9D%B4%EC%A7%80%2088cbdfb849fa835cb23001d2fd345068.md)
기능설명: 사용 중지
도메인: admin/coupon
메서드: PATCH
사용자/관리자: 관리자

### Request

---

**Request Body**

| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `is_active` | bool | Y | `false` = 사용중지 / `true` = 재활성 (coupon.is_active) |

```json
{ "is_active": false }
```

### Response

---

`200` 상태 변경 성공

```json
{
  "id": 5,
  "is_active": false,
  "status": "STOPPED",
  "updated_at": "2026-07-02T10:00:00Z"
}
```

**사용중지 시 서버 동작**

- 쿠폰 `is_active`를 false로 변경 (파생 status=STOPPED)
- 이벤트게시판에서 신규 다운로드 차단
- 회원가입 자동발급 대상에서 제외
- **이미 사용자 쿠폰함(user_coupon)에 있는 쿠폰은 그대로 사용 가능** (사용자 보호)

`400` 허용되지 않는 상태값 · `403` 관리자 권한 없음 · `404` 없음