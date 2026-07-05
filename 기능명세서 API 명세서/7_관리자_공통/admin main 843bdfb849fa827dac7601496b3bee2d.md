# /admin/main

개발순서: 1차
기능: 관리자_대시보드 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EA%B4%80%EB%A6%AC%EC%9E%90_%EB%8C%80%EC%8B%9C%EB%B3%B4%EB%93%9C%2061dbdfb849fa83b09a6c0182c69cef0c.md)
기능설명:  대시보드
도메인: admin
메서드: GET
사용자/관리자: 관리자

Response

---

**`200`** 

```json
{
  "today_orders": 12,
  "today_sales": 340000,
  "new_members": 5,
  "low_stock_count": 3,
  "pending_qna": 6
}
```