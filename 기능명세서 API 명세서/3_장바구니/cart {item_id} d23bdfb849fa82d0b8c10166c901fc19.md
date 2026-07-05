# /cart/{item_id}

개발순서: 1차
기능: 장바구니 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EC%9E%A5%EB%B0%94%EA%B5%AC%EB%8B%88%204edbdfb849fa83b19a990144f3d9b7fc.md)
기능설명: 수량변경
도메인: user/cart
메서드: PATCH
사용자/관리자: 사용자

### Request

---

#### `Request Body`

```json
{"quantity": 3}
```

### Response

---

**`200`** 

**`422` 재고 초과**