# SnackDeal — Backend

> 과자 쇼핑몰 풀스택 프로젝트의 백엔드 API 서버
> Spring Boot 4.1 · Java 25 · MySQL 8 · Redis 7 · Docker

<p>
  <img src="https://img.shields.io/badge/Java-25-orange" />
  <img src="https://img.shields.io/badge/Spring%20Boot-4.1-6DB33F" />
  <img src="https://img.shields.io/badge/MySQL-8-4479A1" />
  <img src="https://img.shields.io/badge/Redis-7-DC382D" />
</p>

---

## 목차

- [프로젝트 소개](#프로젝트-소개)
- [관련 리포지토리](#관련-리포지토리)
- [기술 스택](#기술-스택)
- [시스템 아키텍처](#시스템-아키텍처)
- [ERD](#erd)
- [도메인 구조](#도메인-구조)
- [핵심 설계 결정](#핵심-설계-결정)
- [API 요약](#api-요약)
- [로컬 실행](#로컬-실행)
- [구현 현황](#구현-현황)
- [트러블슈팅](#트러블슈팅)
- [팀](#팀)

---

## 프로젝트 소개

회원가입 · 상품 · 장바구니 · 주문/결제 · 쿠폰 · 고객센터까지 갖춘 온라인 과자 쇼핑몰입니다.
백엔드는 REST API 서버로, 프론트엔드(React)와 AI 챗봇 서비스(FastAPI)를 연결하는 중심 역할을 합니다.

| 데모 | 링크 |
|---|---|
| 사용자 사이트 | `https://...` |
| 관리자 사이트 | `https://...` |
| API 문서 (Swagger) | `https://.../swagger-ui.html` |

## 관련 리포지토리

| 리포 | 설명 |
|---|---|
| [SnackDeal_backand](https://github.com/SnackDeal/SnackDeal_backand) | **현재 리포** · Spring Boot REST API |
| [SnackDeal_react](https://github.com/SnackDeal/SnackDeal_react) | React SPA (사용자 / 관리자) |
| [SnackDeal_ai](https://github.com/SnackDeal/SnackDeal_ai) | FastAPI 기반 AI 챗봇 · 답변 추천 |

---

## 기술 스택

| 구분 | 사용 기술 |
|---|---|
| Language / Framework | Java 25, Spring Boot 4.1 |
| Security | Spring Security, JWT, OAuth2 (Google) |
| Persistence | Spring Data JPA, MySQL 8 |
| Cache / Session | Redis 7 |
| Build | Gradle |
| Infra | Docker, Docker Compose, Nginx Proxy Manager |
| CI/CD | GitHub Actions (self-hosted runner), GHCR |
| Monitoring | Prometheus, Grafana, mysql-exporter, redis-exporter |
| Payment | PortOne SDK → TossPayments (테스트 모드) |

---

## 시스템 아키텍처

```
React (SPA)  →  Nginx (프록시 · SSL)  →  Spring Boot (REST API)  →  MySQL 8
                                                │                    Redis 7
                                                │
                                                └── POST /chatbot/ask → AI 서비스 (FastAPI · Groq · LangGraph)
```

- **CI/CD** — git push → Gradle 빌드 → Docker 이미지(GHCR) → `deploy.sh` 배포
- **Auth** — JWT + Redis 세션 · Google OAuth2
- **Monitoring** — Prometheus 수집 → Grafana 시각화

> 아키텍처 다이어그램 이미지: `docs/architecture.png`

---

## ERD

![ERD](docs/erd.png)

회원 · 상품 · 주문(3테이블 분리) · 배송지 · 쿠폰 게시판/쿠폰함 구조입니다.

### 주요 설계 포인트

| 항목 | 내용 |
|---|---|
| 주문 3테이블 분리 | `orders`(주문) / `shipping`(배송 스냅샷) / `payment`(결제) — 관심사 분리 |
| 배송지 분리 | `delivery`(재사용 주소록) ↔ `shipping`(주문 시점 스냅샷) |
| 쿠폰 1:N | `coupon_board`(이벤트) 1개가 `coupon` 여러 개를 포함. `board_id`는 nullable(가입 쿠폰 = null) |
| 소프트 삭제 | `deleted_at` 컬럼으로 상품/카테고리 이력 보존 |
| enum 일관성 | `FIXED`/`PERCENT`, `EVENT`/`SIGNIN`, 모든 PK는 BIGINT |

### 주문 상태머신 (`orders.status`)

```
PENDING_PAYMENT → PAYMENT_COMPLETED → PREPARING_SHIPMENT → SHIPPED → COMPLETED
        │                  │
        └── CANCELLED      └── REFUND_REQUESTED → REFUND_COMPLETED
```

---

## 도메인 구조

`io.snackdeal.backand` 하위에 도메인별로 `entity · repository · service · controller`를 구성했습니다.

```
io.snackdeal.backand
├── member      회원 · 인증 · OAuth2
├── product     상품 · 카테고리
├── cart        장바구니
├── order       주문 · 결제 · 배송
├── delivery    배송지 주소록
├── coupon      쿠폰게시판 · 쿠폰함
├── cs          공지 · FAQ · QNA · 챗봇 연동
├── admin       관리자 대시보드
└── global      공통 설정 · 예외 · 유틸
```

---

## 핵심 설계 결정

### 1. JWT + Redis 세션으로 중복 로그인 차단

이메일을 로그인 식별자로 사용하고, 로그인 시 Redis에 세션 ID(`sid`)를 저장합니다.

```
로그인 → 토큰 발급(Access·Refresh) → Redis 저장(session:{email}=sid) → 요청마다 토큰 sid 일치 검증
```

- 새 로그인이 발생하면 `sid`를 교체 → 이전 토큰 자동 무효화
- Refresh 토큰도 Redis에 저장, 재발급/로그아웃 시 삭제
- 사용자/관리자 분리 — 프론트는 상태·라우트·레이아웃 분리, 백엔드는 `role == ADMIN` 검증

### 2. 동시성 제어 — 비관적 락

마지막 남은 재고/쿠폰을 여러 명이 동시에 요청하면 중복 발급·초과 판매가 발생합니다.

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
Optional<Product> findByIdForUpdate(Long id);
```

- DB 행 잠금(`SELECT ... FOR UPDATE`)으로 한 번에 한 요청만 처리 — 재고 차감 · 쿠폰 발급
- 락 안에서 `issued < total` 을 원자적으로 검증
- 서버 다중화 시에는 Redis 분산 락(SETNX + TTL) 도입 예정

### 3. 결제 — 서버 재검증

```
1. POST /order/prepare   주문 생성 · 금액 계산 → PENDING_PAYMENT
2. 포트원 SDK            토스페이먼츠(테스트 모드) 결제창
3. POST /order/complete  imp_uid로 결제 금액 위변조 검증
4. 검증 성공 → PAYMENT_COMPLETED + 재고 차감(락) / 미결제 시 즉시 CANCELLED
```

결제 금액은 클라이언트를 신뢰하지 않고 서버에서 다시 검증합니다.

### 4. 관리자 대시보드 Redis 캐싱

무거운 집계 쿼리를 매번 실행하는 대신 `@Cacheable`로 결과를 캐싱합니다.

| 캐시 이름 | TTL |
|---|---|
| `dashboard:summary` | 60s |
| `dashboard:memberChart` | 10min |
| `dashboard:orderChart` | 10min |
| `dashboard:salesChart` | 10min |
| `dashboard:couponChart` | 10min |

- 직렬화: `GenericJackson2JsonRedisSerializer` + `JavaTimeModule` (LocalDate/LocalDateTime 지원, null 캐싱 비활성화)
- 캐시 키: summary = 오늘 날짜 / chart = 조회 시작일 + 종료일
- 현재는 TTL 기반 무효화만 적용, 주문/가입 이벤트 시 `@CacheEvict` 즉시 무효화는 예정

### 5. 배치 스케줄러 (`@Scheduled`)

| 주기 | cron | 작업 |
|---|---|---|
| 10분마다 | `0 */10 * * * *` | 유예시간 초과한 미결제 주문 자동 취소 → 재고 회수 (관리자 수동 변경 건 `manualOverride` 제외) |
| 매일 03시 | `0 0 3 * * *` | 상품별 총 판매수량 집계를 `product` 테이블에 반영 |

---

## API 요약

| 도메인 | 메서드 | 엔드포인트 | 설명 |
|---|---|---|---|
| Auth | POST | `/auth/signup` | 회원가입 (이메일 인증) |
| Auth | POST | `/auth/login` | 로그인 · 토큰 발급 |
| Auth | POST | `/auth/reissue` | Access 토큰 재발급 |
| Product | GET | `/products` | 상품 목록 (카테고리·검색·정렬) |
| Product | GET | `/products/{id}` | 상품 상세 |
| Cart | GET/POST/DELETE | `/cart` | 장바구니 조회·담기·삭제 |
| Order | POST | `/order/prepare` | 주문 생성 · 금액 계산 |
| Order | POST | `/order/complete` | 결제 검증 · 주문 확정 |
| Coupon | GET | `/coupons/boards` | 진행 중인 쿠폰 이벤트 |
| Coupon | POST | `/coupons/{id}/issue` | 쿠폰 발급 |
| CS | GET | `/notices`, `/faqs`, `/qna` | 공지 · FAQ · 문의 |
| Admin | GET | `/admin/dashboard/summary` | 대시보드 요약 지표 |

> 전체 명세는 Swagger UI 참고

---

## 로컬 실행

### 요구 사항

- JDK 25
- Docker / Docker Compose

### 1. 환경 변수 설정

```bash
cp .env.example .env
```

```dotenv
# .env.example
DB_URL=jdbc:mysql://localhost:3306/snackdeal
DB_USERNAME=
DB_PASSWORD=
REDIS_HOST=localhost
REDIS_PORT=6379
JWT_SECRET=
GOOGLE_CLIENT_ID=
GOOGLE_CLIENT_SECRET=
PORTONE_API_KEY=
PORTONE_API_SECRET=
AI_SERVICE_URL=http://localhost:8000
```

### 2. 인프라 기동

```bash
docker compose up -d   # MySQL · Redis · (선택) AI 서비스
```

### 3. 애플리케이션 실행

```bash
./gradlew bootRun
```

기본 포트: `http://localhost:8080`

---

## 구현 현황

- [x] 회원가입 / 로그인 / JWT + Redis 세션 / Google OAuth2
- [x] 상품 · 카테고리 CRUD, 검색 · 정렬 (인기순 비정규화)
- [x] 장바구니
- [x] 주문 3테이블 구조 · 상태머신
- [x] 쿠폰 게시판 / 쿠폰함 발급
- [x] 공지 · FAQ · QNA
- [x] 관리자 대시보드 + Redis 캐싱
- [x] 배치 스케줄러 2종
- [x] Docker Compose · GitHub Actions CI/CD
- [x] Prometheus / Grafana 모니터링
- [ ] 토스페이먼츠 **정식 연동** (현재 테스트 모드)
- [ ] 재고 차감 · 쿠폰 검증 비즈니스 로직 마무리
- [ ] Redis 분산 락 (선착순 쿠폰 대기열)
- [ ] `@CacheEvict` 기반 즉시 캐시 무효화
- [ ] 동시성 부하 테스트

---

## 트러블슈팅

<details>
<summary><b>ISSUE 01 — 대시보드 API 병렬 호출 시 데이터 정합성 불일치</b></summary>

**문제** · 요약 지표와 차트 API를 동시에 호출하면 응답 시점이 어긋나 수치가 맞지 않음
**해결** · Redis 캐싱 + 차등 TTL을 적용하고, 캐시 키를 조회 구간에 맞춰 동일 스냅샷 기반으로 응답하도록 개선
</details>

<details>
<summary><b>ISSUE 02 — 재고 동시성 경쟁 상태</b></summary>

**문제** · 마지막 1개 재고를 여러 사용자가 동시에 주문하면 재고가 음수로 떨어짐
**해결** · 비관적 락으로 행을 잠근 뒤 수량 검증. 분산 환경 확장 시 Redis 분산 락 도입 예정
</details>

<details>
<summary><b>ISSUE 03 — CI/CD 파이프라인 파편화</b></summary>

**문제** · 백엔드 · 프론트엔드 · AI가 서로 다른 런타임과 리포를 사용해 배포가 파편화됨
**해결** · GitHub Actions로 빌드 후 Docker 이미지를 만들어 자동 배포하는 파이프라인으로 통일
</details>

<details>
<summary><b>ISSUE 04 — 인기순 정렬 조회 성능 저하</b></summary>

**문제** · 인기순 정렬을 위해 주문 상세 테이블을 매번 JOIN·집계 → 주문이 늘수록 성능 급격히 저하
**원인** · 인기 기준 데이터가 주문 테이블에 있어, 정규화 구조상 매 요청마다 무거운 집계가 불가피
**해결**
- 상품 테이블에 최근 판매량 컬럼을 비정규화로 추가
- 스케줄러가 매일 새벽 3시에 주문 데이터 기반으로 갱신
- 목록 조회 시 해당 컬럼으로 바로 정렬하여 집계 쿼리 제거

**결과** · 조회 로직이 단순해지고 무거운 집계는 배치로 분리. 조회 빈도와 성능 요구에 따라 비정규화를 선택하는 설계 판단 경험
</details>

---
