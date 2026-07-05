# /admin/qna/{id}

개발순서: 2차
기능: 관리자_QNA관리 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EA%B4%80%EB%A6%AC%EC%9E%90_QNA%EA%B4%80%EB%A6%AC%20358bdfb849fa8226977d011e6ffec3a7.md)
기능설명: 문의 상세
도메인: admin/qna
메서드: GET
사용자/관리자: 관리자

### Response

---

`200`

```json
{
  "id": 45,
  "type": "SHIPPING",
  "title": "주문한 상품이 아직 안 왔어요",
  "content": "6월 28일에 주문했는데 아직도 배송준비중으로 떠 있어요. 언제쯤 받을 수 있을까요? 급하게 필요한 거라 확인 부탁드립니다.",
  "attachment_url": null,
  "ai_summary": "3일 전 주문(0628-00098) 배송 지연 문의. 송장번호 확인 요청.",
  "status": "pending",
  "created_at": "2026-07-01T14:20:00Z",
  "buyer": {
    "id": 12,
    "email": "hong@test.com",
    "name": "홍길동",
    "total_order_count": 7
  },
  "answer": null
}
```

답변이 이미 등록된 경우

```json
{
  ...
  "status": "done",
  "answer": {
    "id": 22,
    "content": "안녕하세요 홍길동님, 확인 결과 폭우로 인해 배송이 지연되었습니다...",
    "answered_at": "2026-07-01T16:40:00Z",
    "answered_by": "관리자"
  }
}
```

`404` 존재하지 않는 문의 · `403` 관리자 권한 없음