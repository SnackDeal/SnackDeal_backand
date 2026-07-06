# /product/{product_id}

개발순서: 1차
기능: 상품 상세 페이지 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EC%83%81%ED%92%88%20%EC%83%81%EC%84%B8%20%ED%8E%98%EC%9D%B4%EC%A7%80%20660bdfb849fa82a2845c8181cfad48dd.md)
기능설명: 상품 상세
도메인: user/product
메서드: GET
사용자/관리자: 사용자

## Response

---

**`200`** 

```json
{
  "id": 1,
  "name": "허니버터 프레첼",
  "price": 4500,
  "description": "달콤한 허니버터 시즈닝이 코팅된 바삭한 프레첼입니다.",
  "image_url": "https://.../1.jpg",
  "stock": 2,
  "is_soldout": false,
  "status": "ACTIVE",
  "category_id": 3,
  "category": "스낵"
}
```

**`404`** 존재하지 않는 상품 (판매중지 상품도 `404`)