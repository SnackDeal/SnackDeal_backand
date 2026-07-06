# /product/list

개발순서: 1차
기능: 상품리스트페이지 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EC%83%81%ED%92%88%EB%A6%AC%EC%8A%A4%ED%8A%B8%ED%8E%98%EC%9D%B4%EC%A7%80%2065dbdfb849fa82fbb6500110b7b424fd.md)
기능설명: 상품 리스트
도메인: user/product
메서드: GET
사용자/관리자: 사용자

### Request

---

#### `Query Params`

| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| category_id | int | N | 카테고리 필터 (category.id)  |
| sort | string | N | `latest`(기본)/`price_asc`/`price_desc`/`popular` |
| keyword | string | N | 상품명 검색 |
| page | int | N | 페이지 (기본 1) |
| size | int | N | 페이지당 개수 (기본 20) |

### Response

---

**`200`** 

```json
{
  "items": [
    {
      "id": 1,
      "name": "허니버터 프레첼",
      "price": 4500,
      "thumbnail_url": "https://.../thumb.jpg",
      "category_id": 3,
      "category": "스낵",
      "is_soldout": false
    }
  ],
  "page": 1,
  "size": 20,
  "total": 24
}
```