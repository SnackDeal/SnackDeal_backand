# /admin/product/{id}

개발순서: 1차
기능: 관리자_상품 등록 / 수정 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EA%B4%80%EB%A6%AC%EC%9E%90_%EC%83%81%ED%92%88%20%EB%93%B1%EB%A1%9D%20%EC%88%98%EC%A0%95%2025fbdfb849fa834893e8019a956630aa.md)
기능설명:  상품 수정
도메인: admin/product
메서드: PUT
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
| image_url | string | Y | 대표 이미지 1장 URL → product_image 1행 갱신 |
| status | string | Y | `ACTIVE`(판매중) / `INACTIVE`(판매중지) |

```json
{
  "name": "허니버터 프레첼 (리뉴얼)",
  "price": 4800,
  "category_id": 3,
  "description": "레시피가 개선된 신제품입니다.",
  "stock": 50,
  "status": "ACTIVE",
  "image_url": "https://.../new1.jpg"
}
```

> 과자 쇼핑몰은 **상품 옵션이 없고 이미지도 1장**이다 → 옵션·다중이미지 필드/규칙 없음. 주문 이력의 상품명은 `order_item.product_name` 스냅샷으로 보존

### Response

---

**`200`** 
수정 성공 → `GET /admin/product/{id}`와 동일한 형식으로 최신 상품 정보 반환

```json
{
  "id": 12,
  "name": "허니버터 프레첼 (리뉴얼)",
  "category_id": 3,
  "category": "스낵",
  "price": 4800,
  "stock": 50,
  "status": "ACTIVE",
  "image_url": "https://.../new1.jpg",
  "updated_at": "2026-07-02T09:15:00Z"
}
```

**`400`** 필수값 누락 / 음수

```json
{
	"error": {
		"code": "BAD_REQUEST"
		"message": "재고 수량은 0 이상이어야 합니다"
	}
}
```

**`404`**존재하지 않는 상품 **`403`** 관리자 권한 없음

**비고**

- 이미지 파일 업로드는 별도 API(`POST /admin/upload/image` 등)로 처리 → URL만 이 API에 전달
- 재고 수량은 수정 화면에서 직접 조정 가능 (별도 재고 관리 화면 없음)
- 수정 시점의 재고 값으로 즉시 반영 (동시성 이슈는 관리자 화면 특성상 낮음, 필요 시 낙관적 락)