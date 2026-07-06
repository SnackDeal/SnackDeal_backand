# admin/product

개발순서: 1차
기능: 관리자_상품관리 리스트(재고 포함) (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EA%B4%80%EB%A6%AC%EC%9E%90_%EC%83%81%ED%92%88%EA%B4%80%EB%A6%AC%20%EB%A6%AC%EC%8A%A4%ED%8A%B8(%EC%9E%AC%EA%B3%A0%20%ED%8F%AC%ED%95%A8)%207dabdfb849fa826a985b81169ad782e0.md)
기능설명: 상품 리스트(재고 포함)
도메인: admin/product
메서드: GET
사용자/관리자: 관리자

### Request

---

#### `Query Params`

| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| keyword | string | N | 상품명 검색 |
| category_id | int | N | 카테고리 필터 (category.id) |
| `status` | string | N | `ACTIVE`(판매중) / `INACTIVE`(판매중지) (품절은 stock==0 파생) |
| low_stock | bool | N | 재고 부족(10개 이하)만 |
| sort | string | N | `latest`/`stock_asc`/`stock_desc`/`price` |
| `page`, `size` | int | N | 페이지네이션 |

### Response

---

**`200`** 

```json
{
  "items": [
    {
      "id": 1, "name": "허니버터 프레첼",
      "category_id": 3, "category": "스낵", "price": 4500,
      "stock": 2, "status": "ACTIVE", "thumbnail_url": "https://..."
    }
  ],
  "page": 1, "size": 20, "total": 24
}
```