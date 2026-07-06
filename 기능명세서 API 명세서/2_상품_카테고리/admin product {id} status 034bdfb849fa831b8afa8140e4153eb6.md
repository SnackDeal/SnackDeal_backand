# /admin/product/{id}/status

개발순서: 1차
기능: 관리자_상품관리 리스트(재고 포함) (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EA%B4%80%EB%A6%AC%EC%9E%90_%EC%83%81%ED%92%88%EA%B4%80%EB%A6%AC%20%EB%A6%AC%EC%8A%A4%ED%8A%B8(%EC%9E%AC%EA%B3%A0%20%ED%8F%AC%ED%95%A8)%207dabdfb849fa826a985b81169ad782e0.md), 관리자_상품 등록 / 수정 (../%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C/%EA%B4%80%EB%A6%AC%EC%9E%90_%EC%83%81%ED%92%88%20%EB%93%B1%EB%A1%9D%20%EC%88%98%EC%A0%95%2025fbdfb849fa834893e8019a956630aa.md)
기능설명: 판매 상태 변경
도메인: admin/product
메서드: PATCH
사용자/관리자: 관리자

### Request

---

#### `Body`

| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| status | string | Y | `ACTIVE`(판매중) / `INACTIVE`(판매중지) / `DELETED`(삭제)  |

```json
{ "status": "INACTIVE" }
```

### Response

---

**`200`** 상태 변경 성공

```json
{
  "id": 12,
  "status": "INACTIVE",
  "updated_at": "2026-07-02T09:20:00Z"
}
```

**`400`** 잘못된 상태값

```json
{
	"error": {
		"code": "BAD_REQUEST"
		"message": "허용되지 않는 상태값입니다"
	}
}
```

**`404`** 존재하지 않는 상품  **`403`** 관리자 권한 없음

**비고**

- 이 API로 변경 가능한 상태는 `ACTIVE` ↔ `INACTIVE`, 그리고 `DELETED`(삭제 시 deleted_at 기록)
- **품절 상태는 이 API로 지정할 수 없음** — 품절은 `stock == 0`일 때 자동 처리되는 파생 상태
- 판매중지(`INACTIVE`)·삭제(`DELETED`) 상품은 사용자 리스트/상세 페이지에서 완전히 노출되지 않음 (`404` 처리)
- 이미 장바구니에 담긴 판매중지 상품은 결제 진행 시 차단 (전에 정한 정책 유지)