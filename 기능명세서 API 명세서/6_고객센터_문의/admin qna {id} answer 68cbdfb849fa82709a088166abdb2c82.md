# /admin/qna/{id}/answer

개발순서: 2차
기능: 관리자_QNA관리 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EA%B4%80%EB%A6%AC%EC%9E%90_QNA%EA%B4%80%EB%A6%AC%20358bdfb849fa8226977d011e6ffec3a7.md)
기능설명: 답변등록
도메인: admin/qna
메서드: POST
사용자/관리자: 관리자

### Request

---

**Request Body**

| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `content` | string | Y | 답변 내용 (10자 이상 권장) |

```json
{ "content": "안녕하세요, 확인 결과 폭우로 인해 배송이 지연되었습니다. 오늘 오후 발송 예정이며 내일까지 도착 예정입니다. 불편을 드려 죄송합니다." }
```

### Response

---

`201` 등록 성공 → 문의 상태 자동으로 `done`으로 변경

```json
{
  "qna_id": 45,
  "answer": {
    "id": 22,
    "content": "안녕하세요, 확인 결과...",
    "answered_at": "2026-07-01T16:40:00Z"
  },
  "qna_status": "done"
}
```

`400` 내용 누락 또는 너무 짧음

```json
{ "error": { "code": "BAD_REQUEST", "message": "답변 내용을 입력해주세요" } }
```

`409` 이미 답변 등록됨

```json
{ "error": { "code": "CONFLICT", "message": "이미 답변이 등록된 문의입니다" } }
```

> 등록 후 수정/삭제 API는 제공하지 않음 (전에 정한 정책 유지)
> 

`403` 관리자 권한 없음 · `404` 존재하지 않는 문의

**서버 동작**

- 트랜잭션으로: `qna_answer` INSERT (qna당 1건 UNIQUE) + `qna.is_answered = true` UPDATE
- 사용자에게 답변 알림은 MVP 제외 (필요 시 이메일 발송이나 마이페이지 배지 정도)