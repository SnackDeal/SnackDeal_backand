# /cs/notice/list

개발순서: 3차
기능: 공지사항게시판 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EA%B3%B5%EC%A7%80%EC%82%AC%ED%95%AD%EA%B2%8C%EC%8B%9C%ED%8C%90%203f8bdfb849fa82fd9c948183d1a869d6.md)
기능설명: 공지 목록
도메인: user/cs
메서드: GET
사용자/관리자: 사용자

> notice 테이블: id, title(50), content(TEXT), is_pinned(t/f), created_at, updated_at, deleted_at

### Request

---

#### `Query Params`

| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| keyword | int | N | 제목 검색 |
| page | int | N | 페이지 (기본 1) |
| size | int | N | 페이지당 개수 (기본 10) |

### Response

---

**`200`** 

```json
{
  "notices": [
    {
      "id": 12,
      "title": "여름 배송 지연 안내",
      "is_pinned": true,
      "created_at": "2026-06-28T10:00:00Z"
    },
    {
      "id": 11,
      "title": "신규 상품 입고 소식",
      "is_pinned": false,
      "created_at": "2026-06-25T14:20:00Z"
    }
  ],
  "page": 1,
  "size": 10,
  "total": 24
}
```

`is_pinned=true`인 공지는 최상단에 고정, 그다음은 최신순 정렬