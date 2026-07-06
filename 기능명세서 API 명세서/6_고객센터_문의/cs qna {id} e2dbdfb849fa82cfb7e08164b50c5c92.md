# /cs/qna/{id}

개발순서: 2차
기능: 문의하기 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EB%AC%B8%EC%9D%98%ED%95%98%EA%B8%B0%207f1bdfb849fa83c997e7015eb095c2c4.md)
기능설명: 문의 상세
도메인: user/cs
메서드: GET
사용자/관리자: 사용자

문의 원문과 (있으면) 관리자 답변을 함께 반환.

### Response

---

**`200`** 

```json
{
  "id": 45,
  "type": "SHIPPING",
  "title": "주문한 상품이 아직 안 왔어요",
  "content": "6월 28일에 주문했는데 아직도 배송준비중으로 떠 있어요. 언제쯤 받을 수 있을까요?",
  "attachment_url": null,
  "status": "done",
  "created_at": "2026-07-01T14:20:00Z",
  "answer": {
    "content": "안녕하세요 홍길동님, 확인 결과 폭우로 인해 배송이 지연되었습니다. 오늘 오후 발송 예정이며 내일까지 도착할 예정입니다. 불편을 드려 죄송합니다.",
    "answered_at": "2026-07-01T16:40:00Z"
  }
}
```

답변이 아직 없으면 `answer`는 `null`

```json
{
  "id": 46,
  "type": "PRODUCT",
  "title": "재입고 문의드려요",
  "content": "허니버터 프레첼 대용량 언제 재입고 되나요?",
  "attachment_url": null,
  "status": "pending",
  "created_at": "2026-07-02T09:00:00Z",
  "answer": null
}
```

401 미인증 403 타인 문의 접근 차단

```json
{ "error": 
	{ 
		"code": "FORBIDDEN", 
		"message": "본인의 문의만 조회할 수 있습니다" 
	} 
}
```