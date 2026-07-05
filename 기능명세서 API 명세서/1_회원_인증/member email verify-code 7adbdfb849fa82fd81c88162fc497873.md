# /member/email/verify-code

개발순서: 1차
기능: 이메일인증 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EC%9D%B4%EB%A9%94%EC%9D%BC%EC%9D%B8%EC%A6%9D%204aebdfb849fa835c872b817a1b7978b6.md)
기능설명: 인증코드 검증
도메인: user/member
메서드: POST
사용자/관리자: 사용자

### Request

---

**Request Body**

| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `email` | string | Y | 인증 대상 이메일 |
| `code` | string | Y | 이메일로 받은 6자리 인증코드 |

```json
{
	"email": "test@test.com", 
	"code": "482913"
}
```

### Response

---

**`200`** 검증 성공 — 회원가입 시 사용할 인증 토큰 발급 (유효시간 10분)

```json
{
	"verification_token": "evt_a1b2c3d4e5...", #이메일 인증 성공 시 발급된 토큰 (email_verification.verification_token)
  "expires_in": 600
}
```

**`400`** 코드 불일치 / 만료

```json
{
	"error": {
		"code": "BAD_REQUEST"
		"message": "인증코드가 일치하지 않습니다"
	}
}
```