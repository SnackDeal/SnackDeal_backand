# /admin/qna

개발순서: 2차
기능: 관리자_QNA관리 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EA%B4%80%EB%A6%AC%EC%9E%90_QNA%EA%B4%80%EB%A6%AC%20358bdfb849fa8226977d011e6ffec3a7.md)
기능설명: 문의 리스트
도메인: admin/qna
메서드: GET
사용자/관리자: 관리자

### Request

---

**Query Params**

| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `type` | string | N | `ORDER`/`SHIPPING`/`PRODUCT`/`OTHER` (qna.type) |
| `status` | string | N | `pending`(답변 대기) / `done`(답변 완료) — qna.is_answered 파생 |
| `keyword` | string | N | 제목/내용 검색 |
| `date_from` | date | N | 접수일 시작 |
| `date_to` | date | N | 접수일 종료 |
| `page` | int | N | 페이지 (기본 1) |
| `size` | int | N | 페이지당 개수 (기본 20) |

### Response

---

`200`

```json
{
  "items": [
    {
      "id": 45,
      "type": "SHIPPING",
      "title": "주문한 상품이 아직 안 왔어요",
      "buyer_id": 12,
      "buyer_email": "hong@test.com",
      "buyer_name": "홍길동",
      "ai_summary": "3일 전 주문 배송 지연 문의. 송장번호 확인 요청.",
      "ai_summary_status": "done",
      "status": "pending",
      "has_attachment": false,
      "created_at": "2026-07-01T14:20:00Z"
    },
    {
      "id": 44,
      "type": "PRODUCT",
      "title": "알레르기 성분 문의",
      "buyer_id": 8,
      "buyer_email": "kim@test.com",
      "buyer_name": "김영희",
      "ai_summary": null,
      "ai_summary_status": "failed",
      "status": "pending",
      "has_attachment": true,
      "created_at": "2026-07-01T11:05:00Z"
    }
  ],
  "page": 1,
  "size": 20,
  "total": 46
}
```

**필드 설명**

- `ai_summary` / `ai_summary_status`: (선택 기능) qna 테이블에 저장되는 컬럼이 아니라 서버가 생성/캐시하는 파생 값 — MVP에서 생략 가능
- `has_attachment`: qna.attachment_url 유무 (리스트에서 아이콘 표시용)