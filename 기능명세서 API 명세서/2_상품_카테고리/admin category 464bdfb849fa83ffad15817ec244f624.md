# /admin/category

개발순서: 3차
기능: 관리자_카테고리 페이지 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EA%B4%80%EB%A6%AC%EC%9E%90_%EC%B9%B4%ED%85%8C%EA%B3%A0%EB%A6%AC%20%ED%8E%98%EC%9D%B4%EC%A7%80%20b1fbdfb849fa835fb69881cc4d460330.md)
기능설명:  카테고리 추가
도메인: admin/category
메서드: POST
사용자/관리자: 관리자

### Request

---

**Request Body**

| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `name` | string | Y | 카테고리명 (2~50자) |

```json
{ "name": "젤리" }
```

### Response

---

**`201`** 등록 성공 — `sort_order`는 기존 최댓값 + 1로 자동 부여

```json
{
  "id": 5,
  "name": "젤리",
  "sort_order": 5,
  "product_count": 0,
  "active": true
}
```

**`400`** 이름 형식 오류 · `409` 중복 이름

```json
{
	"error": {
		"code": "CONFLICT"
		"message": "이미 존재하는 카테고리명입니다"
	}
}
```

**`409`  관리자 권한 없음**