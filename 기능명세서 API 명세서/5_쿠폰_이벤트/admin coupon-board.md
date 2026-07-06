# /admin/coupon-board (이벤트게시판)

개발순서: 2차
기능: 관리자_쿠폰관리페이지
기능설명: 이벤트게시판(coupon_board) 관리 — 쿠폰을 게시할 이벤트 글 CRUD
도메인: admin/coupon-board
사용자/관리자: 관리자

> coupon.coupon_board_id는 nullable FK다. **EVENT(이벤트) 쿠폰**은 이벤트게시판을 먼저 게시한 뒤 그 게시판에 등록하고, **SIGNIN(회원가입) 쿠폰**은 게시판 없이(null) 등록할 수 있다.
> 게시판 노출 기간 = `start_at ~ end_at`, 쿠폰 다운로드 지정일 = 소속 쿠폰의 `valid_from`.

---

## GET /admin/coupon-board — 게시판 목록

### Response `200`

```json
{
  "boards": [
    {
      "id": 3,
      "title": "여름 이벤트",
      "thumbnail_url": "https://.../summer.jpg",
      "is_active": true,
      "start_at": "2026-07-05T00:00:00",
      "end_at": "2026-08-31T23:59:59",
      "coupon_count": 2,
      "created_at": "2026-07-02T09:00:00Z"
    }
  ]
}
```

---

## POST /admin/coupon-board — 게시판 등록(게시)

메서드: POST

### Request Body

| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `title` | string | Y | 게시판 제목 (100자 이내) |
| `content` | string | Y | 본문 내용 |
| `thumbnail_url` | string | N | 썸네일 이미지 URL |
| `is_active` | bool | N | 활성 여부 (기본 true) |
| `start_at` | datetime | Y | 게시(노출) 시작 |
| `end_at` | datetime | N | 게시(노출) 종료 |

```json
{
  "title": "여름 이벤트",
  "content": "무더운 여름, 시원한 할인 쿠폰을 받아가세요!",
  "thumbnail_url": "https://.../summer.jpg",
  "is_active": true,
  "start_at": "2026-07-05T00:00:00",
  "end_at": "2026-08-31T23:59:59"
}
```

### Response `201`

```json
{ "id": 3, "title": "여름 이벤트", "is_active": true, "created_at": "2026-07-02T09:00:00Z" }
```

`400` 필수값 누락 / `end_at < start_at`

---

## PUT /admin/coupon-board/{id} — 게시판 수정

메서드: PUT · Body는 등록과 동일(수정 가능 필드). `200` 수정된 게시판 반환 · `404` 없음

---

## DELETE /admin/coupon-board/{id} — 게시판 삭제(비활성)

메서드: DELETE · **하드 삭제 대신 `deleted_at` 기록(소프트 삭제)**. 소속 쿠폰이 있으면 경고. `200` 성공 · `422` 소속 쿠폰 존재 시 정책 확인

`403` 관리자 권한 없음
