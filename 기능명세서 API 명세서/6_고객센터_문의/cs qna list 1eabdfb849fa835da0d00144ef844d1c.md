# /cs/qna/list

개발순서: 2차
기능: 문의하기 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EB%AC%B8%EC%9D%98%ED%95%98%EA%B8%B0%207f1bdfb849fa83c997e7015eb095c2c4.md)
기능설명: 내 문의 목록
도메인: user/cs
메서드: GET
사용자/관리자: 사용자

### Request

---

#### `Body`

| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| status | string | N | `pending`(답변 대기) / `done`(답변 완료) — qna.is_answered 파생 |
| type | string | N | `ORDER`/`SHIPPING`/`PRODUCT`/`OTHER` (qna.type) |
| page | int | N | 페이지 (기본 1) |
| size | int | N | 페이지당 개수 (기본 10) |

### Response

---

**`200`** 

```json
{
  "qnas": [
    {
      "id": 45,
      "type": "SHIPPING",
      "title": "주문한 상품이 아직 안 왔어요",
      "status": "pending",
      "is_answered": false,
      "created_at": "2026-07-01T14:20:00Z"
    },
    {
      "id": 42,
      "type": "PRODUCT",
      "title": "알레르기 유발 성분 문의",
      "status": "done",
      "is_answered": true,
      "created_at": "2026-06-28T09:15:00Z",
      "answered_at": "2026-06-28T11:30:00Z"
    }
  ],
  "page": 1,
  "size": 10,
  "total": 5
}
```

**`401` 미인증**