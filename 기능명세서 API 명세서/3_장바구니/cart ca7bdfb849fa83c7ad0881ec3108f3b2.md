# /cart

개발순서: 1차
기능: 장바구니 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EC%9E%A5%EB%B0%94%EA%B5%AC%EB%8B%88%204edbdfb849fa83b19a990144f3d9b7fc.md)
기능설명: 장바구니 담기
도메인: user/cart
메서드: POST
사용자/관리자: 사용자

### Request

---

#### `Request Body`

| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| product_id  | int | Y | 상품ID (product.id)  |
| quantity | int | Y | 수량 (1 이상) |

```json
{ "product_id": 1, "quantity": 2 }
```

> 과자 쇼핑몰은 상품 옵션이 없으므로 option 필드 없음. 동일 상품을 다시 담으면 수량이 합산된다(cart_item은 member+product 단위).

### Response

---

**`201` 담기 성공**

`401` 미인증 · `422` 재고 초과

```json
{ "error": 
	{ 
		"code": "UNPROCESSABLE", 
		"message": "재고가 부족합니다 (최대 2개)"
	}
}
```