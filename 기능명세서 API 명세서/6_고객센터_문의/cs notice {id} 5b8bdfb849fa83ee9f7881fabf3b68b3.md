# /cs/notice/{id}

개발순서: 3차
기능: 공지사항게시판 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EA%B3%B5%EC%A7%80%EC%82%AC%ED%95%AD%EA%B2%8C%EC%8B%9C%ED%8C%90%203f8bdfb849fa82fd9c948183d1a869d6.md)
기능설명: 공지 상세
도메인: user/cs
메서드: GET
사용자/관리자: 사용자

### Response

---

**`200`** 

```json
{
  "id": 12,
  "title": "여름 배송 지연 안내",
  "content": "6월 28일부터 7월 5일까지 폭우로 인해 배송이 1~2일 지연될 수 있습니다...",
  "is_pinned": true,
  "created_at": "2026-06-28T10:00:00Z",
  "updated_at": "2026-06-28T10:00:00Z"
}
```

`404`존재하지 않는 공지

```json
{ "error": 
	{ 
	"code": "NOT_FOUND", 
	"message": "공지사항을 찾을 수 없습니다" 
	} 
}
```