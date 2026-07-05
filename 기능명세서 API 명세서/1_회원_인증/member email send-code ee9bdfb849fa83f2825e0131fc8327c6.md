# /member/email/send-code

개발순서: 1차
기능: 이메일인증 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EC%9D%B4%EB%A9%94%EC%9D%BC%EC%9D%B8%EC%A6%9D%204aebdfb849fa835c872b817a1b7978b6.md)
기능설명: 인증코드 발송
도메인: user/member
메서드: POST
사용자/관리자: 사용자

### Request

---

#### `Request Body`

| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `email`  | string | Y | 인증받을 이메일 |

```json
{ "email": "test@test.com" }
```

### Response

---

**`200`** 인증코드 발송 성공 (유효시간 5분, 60초 후 재발송 가능)

```json
{ "message": "인증코드가 발송되었습니다", "expires_in": 300 }
```

`400` 이메일 형식 오류 `409` 이미 가입된 이메일

```json
{
	"error": {
		"code": "CONFLICT"`
		"message": "이미 가입된 이메일입니다"
	}
}
```

`429` 재발송 대기 필요 (60초 이내 재요청)