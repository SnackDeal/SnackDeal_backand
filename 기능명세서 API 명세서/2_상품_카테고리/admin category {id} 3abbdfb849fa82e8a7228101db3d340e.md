# /admin/category/{id}

개발순서: 3차
기능: 관리자_카테고리 페이지 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EA%B4%80%EB%A6%AC%EC%9E%90_%EC%B9%B4%ED%85%8C%EA%B3%A0%EB%A6%AC%20%ED%8E%98%EC%9D%B4%EC%A7%80%20b1fbdfb849fa835fb69881cc4d460330.md)
기능설명:  이름 수정
도메인: admin/category
메서드: PUT
사용자/관리자: 관리자

### Request

---

#### **Request Body**

| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `name` | string | Y | 새 카테고리명 |

```json
{ "name": "젤리 & 캔디" }
```

### Response

---

**`200`** 수정 성공 — 이 카테고리에 연결된 상품들도 자동으로 새 이름 노출 (외래키 참조)

```json
{
  "id": 5,
  "name": "젤리 & 캔디",
  "updated_at": "2026-07-02T10:00:00Z"
}
```

`400` 형식 오류 · `409` 다른 카테고리와 중복 · `404` 없음 · `403` 권한 없음