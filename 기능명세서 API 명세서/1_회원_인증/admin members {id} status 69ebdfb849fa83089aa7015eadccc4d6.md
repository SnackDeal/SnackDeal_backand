# /admin/members/{id}/status

개발순서: 3차
기능: 관리자_회원 관리 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EA%B4%80%EB%A6%AC%EC%9E%90_%ED%9A%8C%EC%9B%90%20%EA%B4%80%EB%A6%AC%20be0bdfb849fa83b2880a01649d3450d3.md)
기능설명: 회원 상태 변경
도메인: admin/users
메서드: PATCH
사용자/관리자: 관리자

### Request

---

회원 상태를 정상(ACTIVE)/휴면(INACTIVE)/탈퇴(DELETED)로 변경. **하드 삭제하지 않고 상태 변경 + deleted_at 기록으로 처리**하여 주문 이력 등을 보존.

**Request Body**

| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `status` | string | Y | `ACTIVE` / `INACTIVE` / `DELETED` |
| `reason` | string | N | 상태 변경 사유 (관리자 메모용) |

```json
{ "status": "INACTIVE", "reason": "6개월 미접속" }
```

**허용되는 상태 전이**

`ACTIVE ⇄ INACTIVE
   ↓         ↓
  DELETED (한 번 탈퇴하면 되돌릴 수 없음)`

## **Response**

---

`200` 상태 변경 성공

```json
{
  "id": 45,
  "email": "hong@test.com",
  "status": "INACTIVE",
  "updated_at": "2026-07-02T10:00:00Z"
}
```

`400` 허용되지 않는 상태값

```json
{ "error": { "code": "BAD_REQUEST", "message": "허용되지 않는 상태값입니다" } }
```

`403` 본인 계정 변경 차단

```json
{ "error": { "code": "FORBIDDEN", "message": "본인 계정의 상태는 변경할 수 없습니다" } }
```

`422` 잘못된 상태 전이

json

```json
{ "error": { "code": "UNPROCESSABLE", "message": "탈퇴한 회원의 상태는 변경할 수 없습니다" } }
```

**탈퇴(`DELETED`) 처리 시 서버 동작**

- 상태를 `DELETED`로 변경하고 `deleted_at`을 기록
- 로그인 차단 (로그인 API에서 `DELETED` 상태 계정은 `401` 반환)
- 개인정보 마스킹은 선택 (실무는 개인정보 보호법상 일정 기간 후 마스킹/삭제, MVP는 생략)
- 주문 이력, 문의 이력은 그대로 보존
- 세션/토큰 즉시 무효화

`404` 존재하지 않는 회원