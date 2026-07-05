# /member/token/refresh

개발순서: 1차
기능: 로그인 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EB%A1%9C%EA%B7%B8%EC%9D%B8%20626bdfb849fa839fb03201d7b49b4d2f.md)
기능설명: 토큰 재발급
도메인: user/member
메서드: POST
사용자/관리자: 사용자

### Request

---

#### `Request Body`

| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `refresh_token` | string | Y | 리프레시 토큰  |

```json
{
	"access_token" : "yJhbGci..."
}
```

### Response

---

**`400`** 유효하지 않은 리프레시 토큰

```json
{
	"error": {
		"code": ""
		"message": 
	}
}
```