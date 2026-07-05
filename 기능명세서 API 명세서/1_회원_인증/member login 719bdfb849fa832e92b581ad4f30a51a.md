# /member/login

개발순서: 1차
기능: 로그인 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EB%A1%9C%EA%B7%B8%EC%9D%B8%20626bdfb849fa839fb03201d7b49b4d2f.md)
기능설명: 로그인
도메인: user/member
메서드: POST
사용자/관리자: 사용자

### Request

---

#### `Request Body`

| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `email`  | string | Y | 이메일번호  |
| `password` | string | Y | 비밀번호 (8자 이상, 영문 + 숫자 + 특수문자) |

```json
{
	"email" : "test@test.com",
	"password" : "p@ssW@ord!"
}
```

### Response

---

**`200` 로그인** 성공 (서버는 `last_login`을 현재 시각으로 업데이트)

```json
{
	"access_token": "eyJhbGci...",
  "refresh_token": "eyJhbGci...",
  "user": {
    "id": 1,
    "email": "test@test.com",
    "name": "홍길동",
    "role": "USER",
    "created_at": "2026-07-01T00:00:00Z",
    "last_login": "2026-07-01T14:32:00Z"
	}
}
```

`400` 입력값 누락 · `401` 아이디 또는 비밀번호 불일치

```json
{
	"error": {
		"code": "UNAUTHORIZED"
		"message": "아이디 또는 비밀번호가 일치하지 않습니다"
	}
}
```