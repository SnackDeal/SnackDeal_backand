# /member/me

개발순서: 2차
기능: 회원정보 관리 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%ED%9A%8C%EC%9B%90%EC%A0%95%EB%B3%B4%20%EA%B4%80%EB%A6%AC%206dabdfb849fa835f863781c0417aac20.md)
기능설명: 회원 정보 수정
도메인: user/member
메서드: PATCH
사용자/관리자: 사용자

### Request

---

#### `Body`

| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `email` | string | Y | 이메일 |
| `new_password` | string | N | 새 비밀번호 (변경 시) |
| `new_phone` | string | N | 새 휴대폰번호 (하이픈 없이, 변경 시) |

```json
{
	"email" : "test@test.com",
	"new_password" : "p@ssW@ord!",
	"new_phone" : "01011112222"
}
```

> 배송지(주소)는 회원정보가 아닌 **주소록(delivery)** 에서 별도 관리한다.

### Response

---

**`201`** 수정된 user 

`400` 유효성 실패  

`401` 현재 비밀번호 불일치