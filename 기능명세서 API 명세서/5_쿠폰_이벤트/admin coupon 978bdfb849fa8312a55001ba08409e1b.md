# /admin/coupon

개발순서: 2차
기능: 관리자_쿠폰관리페이지 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EA%B4%80%EB%A6%AC%EC%9E%90_%EC%BF%A0%ED%8F%B0%EA%B4%80%EB%A6%AC%ED%8E%98%EC%9D%B4%EC%A7%80%2088cbdfb849fa835cb23001d2fd345068.md)
기능설명: 쿠폰 등록
도메인: admin/coupon
메서드: POST
사용자/관리자: 관리자

### Request

---

#### `Body`

| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `name` | string | Y | 쿠폰명 (50자 이내) |
| `discount_type` | string | Y | `PERCENT` (정률) / `FIXED` (정액) |
| `discount_value` | int | Y | 할인 값 (정률: 1~100, 정액: 원 단위) |
| `min_order_price` | int | N | 최소 주문 금액 (기본 0) |
| `valid_from` | datetime | Y | 다운로드·사용 가능 시작 (지정일) |
| `valid_until` | datetime | N | 유효기간 종료 (없으면 무기한) |
| `total_quantity` | int | Y | 총 발급 수량 (무제한 시 0 또는 매우 큰 값 정책) |
| `issue_type` | string | Y | `EVENT` (이벤트게시판 다운로드) / `SIGNIN` (회원가입 자동) |
| `coupon_board_id` | int | 조건부 | 소속 이벤트게시판(coupon_board) ID. **nullable FK** — `EVENT`는 필수, `SIGNIN`은 생략(null) 가능 |
| `is_active` | bool | N | 활성 여부 (기본 true) |

> `coupon_board_id`는 nullable FK다. **`EVENT`(이벤트) 쿠폰은 반드시 이벤트게시판에 소속**되어야 하므로, 게시판을 먼저 등록(게시)한 뒤 그 게시판에 쿠폰을 등록한다. **게시판 노출 기간은 coupon_board.start_at~end_at**, **쿠폰 다운로드/사용 시작 지정일은 coupon.valid_from** 이다. **`SIGNIN`(회원가입) 쿠폰은 게시판 없이(coupon_board_id=null) 등록 가능**하다.
>
> 과자 쇼핑몰 쿠폰 타입은 **회원가입(SIGNIN) / 할인·이벤트(EVENT)** 2종이며, 별도 코드 입력 방식은 없다.

**이벤트 쿠폰 예시**

```json
{
  "name": "여름맞이 10% 할인",
  "discount_type": "PERCENT",
  "discount_value": 10,
  "min_order_price": 20000,
  "valid_from": "2026-07-10T00:00:00",
  "valid_until": "2026-08-31T23:59:59",
  "total_quantity": 300,
  "issue_type": "EVENT",
  "coupon_board_id": 3
}
```

**회원가입 쿠폰 예시**

```json
{
  "name": "신규가입 3천원",
  "discount_type": "FIXED",
  "discount_value": 3000,
  "min_order_price": 0,
  "valid_from": "2026-01-01T00:00:00",
  "valid_until": "2026-12-31T23:59:59",
  "total_quantity": 100000,
  "issue_type": "SIGNIN",
  "coupon_board_id": null
}
```

### Response

---

`201` 등록 성공

```json
{
  "id": 5,
  "name": "여름맞이 10% 할인",
  "is_active": true,
  "status": "ACTIVE",
  "coupon_board_id": 3,
  "created_at": "2026-07-02T09:00:00Z"
}
```

`400` 필수값 누락 / 유효성 실패

`{ "error": { "code": "BAD_REQUEST", "message": "EVENT 쿠폰은 coupon_board_id가 필수입니다" } }`

기타 유효성 실패 케이스:

- `discount_type=PERCENT`인데 `discount_value`가 0 또는 100 초과
- `valid_until < valid_from` (기간 역전)
- `issue_type=EVENT`인데 coupon_board_id 누락 또는 소속 게시판이 존재하지 않음
- `total_quantity <= 0`

`409` 발급 방식 중복 (선택 정책)

`{ "error": { "code": "CONFLICT", "message": "이미 활성 상태인 회원가입 쿠폰이 있습니다" } }`

> `SIGNIN` 타입은 동시에 여러 개 활성 상태로 두면 신규 회원에게 여러 쿠폰이 지급되어 정책이 혼란스러워짐. 관리자에게 경고만 하고 허용할지, 엄격히 차단할지는 팀에서 결정 (앞에서 논의).
> 

`403` 관리자 권한 없음