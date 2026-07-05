# /admin/members

개발순서: 3차
기능: 관리자_회원 관리 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EA%B4%80%EB%A6%AC%EC%9E%90_%ED%9A%8C%EC%9B%90%20%EA%B4%80%EB%A6%AC%20be0bdfb849fa83b2880a01649d3450d3.md)
기능설명: 회원 리스트
도메인: admin/users
메서드: GET
사용자/관리자: 관리자

### Request

---

#### `Query Params`

| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `keyword` | string | N | 이메일/이름 검색어 |
| `status` | string | N | 상태 필터 (`ACTIVE` / `INACTIVE` / `DELETED`) |

### Response

---

**`200`** 

```json
{
  "members": [
    {
      "id": 1, "email": "test@test.com", "name": "홍길동",
      "created_at": "2026-06-12", "last_login": "2026-07-01",
      "status": "ACTIVE"
    }
  ]
}
```

`status`: `ACTIVE`(정상) / `INACTIVE`(휴면) / `DELETED`(탈퇴)