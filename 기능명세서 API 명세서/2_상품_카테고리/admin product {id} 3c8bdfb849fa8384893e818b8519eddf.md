# /admin/product/{id}

개발순서: 1차
기능: 관리자_상품 등록 / 수정 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EA%B4%80%EB%A6%AC%EC%9E%90_%EC%83%81%ED%92%88%20%EB%93%B1%EB%A1%9D%20%EC%88%98%EC%A0%95%2025fbdfb849fa834893e8019a956630aa.md)
기능설명: 상품 상세 (수정용)
도메인: admin/product
메서드: GET
사용자/관리자: 관리자

### Request

수정 폼에 채울 상품 전체 정보 조회. 사용자용 상품 상세와 달리 **판매중지 상품, 재고 0 상품도 조회 가능**하고, 관리자가 필요한 필드(재고, 상태, 등록/수정 시각 등)를 모두 반환.

---

#### `Path Params`

| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `id` | long | Y | 상품ID |

### Response

---

**`200`** 

```json
{
  "id": 12,
  "name": "허니버터 프레첼",
  "category_id": 3,
  "category": "스낵",
  "price": 4500,
  "description": "달콤한 허니버터 시즈닝이 코팅된 바삭한 프레첼입니다.",
  "stock": 2,
  "status": "ACTIVE",
  "image_url": "https://.../1.jpg",
  "created_at": "2026-06-12T10:30:00Z",
  "updated_at": "2026-06-28T14:20:00Z"
}
```

**`404`** 존재하지 않는 상품

```json
{ "error": 
	{ 
		"code": "NOT_FOUND", 
		"message": "상품을 찾을 수 없습니다" 
	} 
}
```

`403` 관리자 권한 없음

> 과자몰은 옵션 없음, 상품 이미지도 대표 1장(image_url)만 사용
>