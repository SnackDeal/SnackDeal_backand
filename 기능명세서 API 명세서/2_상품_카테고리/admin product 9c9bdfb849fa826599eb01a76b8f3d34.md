# /admin/product

개발순서: 1차
기능: 관리자_상품 등록 / 수정 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EA%B4%80%EB%A6%AC%EC%9E%90_%EC%83%81%ED%92%88%20%EB%93%B1%EB%A1%9D%20%EC%88%98%EC%A0%95%2025fbdfb849fa834893e8019a956630aa.md)
기능설명: 상품 등록
도메인: admin/product
메서드: POST
사용자/관리자: 관리자

### Request

---

#### `Body`

| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| name | string | Y | 상품명 |
| price | int | Y | 판매가 (0 이상) |
| category_id | int | Y | 카테고리 ID (category.id) |
| description | string | N | 상세 설명 |
| stock | int | Y | 재고 수량 (0 이상) |
| image_url | string | Y | 대표 이미지 1장 URL → product_image 1행 저장 |
| status | string | Y | `ACTIVE`(판매중) / `INACTIVE`(판매중지) |

```json
{
  "name": "허니버터 프레첼",
  "price": 4500,
  "category_id": 3,
  "description": "달콤한 허니버터 시즈닝이 코팅된 바삭한 프레첼입니다.",
  "stock": 50,
  "image_url": "https://.../1.jpg",
  "status": "ACTIVE"
}
```

> 과자 쇼핑몰은 **상품 옵션이 없고 이미지도 1장**이다 → 옵션·다중이미지 필드 없음

### Response

---

**`201`** 등록 성공 `400` 필수값 누락/음수