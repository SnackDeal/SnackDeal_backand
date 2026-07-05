# /cs/qna (문의 등록)

개발순서: 2차
기능: 문의하기 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EB%AC%B8%EC%9D%98%ED%95%98%EA%B8%B0%207f1bdfb849fa83c997e7015eb095c2c4.md)
기능설명: 문의 작성(접수)
도메인: user/cs
메서드: POST
사용자/관리자: 사용자

### Request

---

#### `Request Body`

| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `type` | string | Y | 문의 유형 `ORDER`/`SHIPPING`/`PRODUCT`/`OTHER` (qna.type) |
| `title` | string | Y | 제목 (50자 이내) |
| `content` | string | Y | 내용 |
| `attachment_url` | string | N | 첨부 URL |

```json
{
  "type": "SHIPPING",
  "title": "주문한 상품이 아직 안 왔어요",
  "content": "6월 28일에 주문했는데 아직 배송준비중이에요.",
  "attachment_url": null
}
```

### Response

---

**`201`** 접수 성공 (등록 시 `is_answered=false`, `member_id`는 로그인 사용자)

```json
{
  "id": 47,
  "type": "SHIPPING",
  "title": "주문한 상품이 아직 안 왔어요",
  "is_answered": false,
  "created_at": "2026-07-02T10:00:00Z"
}
```

**`400`** 필수값 누락 (유형/제목/내용) · **`401`** 미인증
