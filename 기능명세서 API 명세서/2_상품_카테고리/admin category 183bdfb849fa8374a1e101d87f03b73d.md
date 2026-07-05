# /admin/category

개발순서: 3차
기능: 관리자_카테고리 페이지 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EA%B4%80%EB%A6%AC%EC%9E%90_%EC%B9%B4%ED%85%8C%EA%B3%A0%EB%A6%AC%20%ED%8E%98%EC%9D%B4%EC%A7%80%20b1fbdfb849fa835fb69881cc4d460330.md)
기능설명:  카테고리 리스트
도메인: admin/category
메서드: GET
사용자/관리자: 관리자

### Request

---

**Query Params**

| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `include_inactive` | bool | N | 비활성 카테고리 포함 여부 (기본 `false`) |

### Response

---

**`200`** 

```json
{
  "categories": [
    {
      "id": 1,
      "name": "스낵",
      "sort_order": 1,
      "product_count": 12,
      "active": true
    },
    {
      "id": 2,
      "name": "쿠키",
      "sort_order": 2,
      "product_count": 8,
      "active": true
    },
    {
      "id": 3,
      "name": "웨하스",
      "sort_order": 3,
      "product_count": 4,
      "active": true
    },
    {
      "id": 4,
      "name": "한정판",
      "sort_order": 4,
      "product_count": 0,
      "active": true
    }
  ]
}
```

**필드 설명**

- `product_count`: 이 카테고리에 연결된 상품 수 (삭제 시 경고용)
- `sort_order`: 사용자 상품 리스트에서의 노출 순서