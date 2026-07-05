# /member/join

개발순서: 1차
기능: 회원가입 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%ED%9A%8C%EC%9B%90%EA%B0%80%EC%9E%85%206e5bdfb849fa832fa5b601e4c33873c9.md)
기능설명: 회원가입확인
도메인: user/member
메서드: POST
사용자/관리자: 사용자

### Request

---

#### `Request Body`

| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `email` | string | Y | 이메일 (member.email, UNIQUE) |
| `password` | string | Y | 비밀번호 (8자 이상, 영문 + 숫자 + 특수문자) |
| `name` | string | Y | 이름 (최소 2글자 ~ 최대 20글자) |
| `phone` | string | Y | 휴대폰번호 (하이픈 없이) |
| `birth` | string(date) | Y | 생년월일 (YYYY-MM-DD) |
| `gender` | string | Y | 성별 (`MALE` / `FEMALE`) |
| `verification_token` | string | Y | 이메일 인증 검증 시 발급된 토큰 (email_verification.verification_token) |

```json
{
	"email" : "test@test.com",
	"password" : "p@ssW@ord!",
	"name" : "홍길동",
	"phone" : "01011112222",
	"birth" : "2000-01-01",
	"gender" : "MALE",
	"verification_token" : "evt_a1b2c3d4e5..."
}
```

### Response

---

**`201`** 회원가입 성공 — 가입 시 "회원가입 자동발급" 활성 쿠폰이 있으면 쿠폰함에 함께 지급

```json
{
	"access_token": "eyJhbGci...",
	"refresh_token": "eyJhbGci...",
	"user":	{
		"id" : 1,
		"email" : "test@test.com",
		"name" : "홍길동",
		"phone" : "01011112222",
		"birth" : "2000-01-01",
		"gender" : "MALE",
		"role" : "USER"
	}
}
```

**`401`** 이메일 인증 토큰 누락/만료/불일치 ("이메일 인증이 필요합니다")

**`400`** 유효성 검사 실패

```json
{ "error": 
	{ 
		"code": "BAD_REQUEST", 
		"message": "비밀번호는 8자 이상이어야 합니다" 
	} 
}
```

**`409`** 이미 사용중인 `email`

```json
{
	"error": {
		"code": "CONFLICT"`
		"message": "이미 사용중인 아이디 입니다"
	}
}
```