# SnackDeal_backand

과자 쇼핑몰 SnackDeal의 백엔드. `C:\work\spring\mite88_shop`의 인증/공통 인프라(JWT + Redis 세션 + Google OAuth2)를 이식하고, SnackDeal 전용 스키마(14~18개 테이블)와 도메인 구조로 재구성한 프로젝트. 프론트엔드는 별도 React 프로젝트(`../react`)이며 이 저장소는 API 서버만 제공한다.

## 빌드 및 실행

```bash
# 전체 클린 빌드 (결과물: build/libs/backand-0.0.1-SNAPSHOT.jar)
./gradlew clean build

# 테스트 제외 빌드
./gradlew build -x test

# 전체 테스트 실행
./gradlew test

```

스택: **Spring Boot 4.1.0**, **Java 25**, Gradle. CI/CD(`.github/workflows/deployment-workflow.yml`)는 self-hosted runner에서 실행: Gradle 빌드 → Docker 이미지(GHCR) → `deployment.sh`.

## 활성 프로파일

기본값: `mysql,redis,dev`

| 프로파일 | 설정 파일 | 역할 |
| --- | --- | --- |
| `mysql` | `application-mysql.yml` | MySQL 데이터소스; Flyway 활성화; `ddl-auto: none` |
| `redis` | `application-redis.yml` | Redis(Lettuce) 연결 |
| `h2` | `application-h2.yml` | 인메모리 H2 (로컬/테스트용); `ddl-auto: ${JPA_DDL_AUTO:create}` |
| `google` | `application-google.yml` | Google OAuth2 (선택) |
| `dev` | `application-dev.properties` | 로컬 개발용 환경변수 |
| `prod` | `application-prod.properties` | 운영 환경변수 |

MySQL/Redis 없이 로컬 개발 시 `h2` 프로파일 활성화, `mysql` 제거.

---

## 🐳 Docker 로컬 인프라 실행 가이드

앱 자체는 로컬에서 `./gradlew bootRun`(또는 IDE)으로 띄우고, 의존 인프라(DB, 캐시 등)는 Docker를 통해 한 줄로 쉽게 기동할 수 있습니다.

### 1. Docker Compose 일괄 실행 (권장)

프로젝트 루트 디렉터리에서 아래 명령어를 실행하면 필요한 모든 인프라가 한 번에 기동됩니다.

```bash
# 전체 인프라 백그라운드 실행
docker compose up -d

# 필요한 인프라만 선택 실행 (예: MySQL이 불필요할 때)
docker compose up -d redis prometheus

# 컨테이너 종료 (데이터 유지)
docker compose down

# 컨테이너 종료 + 데이터 완전 삭제 (주의)
docker compose down -v

```

### 2. Docker Run 수동 실행 (개별 기동 시)

Docker Compose를 쓰지 않고 개별적으로 컨테이너를 기동하고 싶다면 다음 명령어를 순서대로 실행합니다.

```bash
# 0. 공통 네트워크 생성
docker network create snackdeal-network

# 1. Redis 실행 (캐시/세션)
docker run -d --name mite88Shop-redis --network snackdeal-network -v redis_data:/data --restart unless-stopped redis:7-alpine

# 2. MySQL 실행 (데이터베이스 / 포트 3306)
docker run -d --name mite88Shop-mysql --network snackdeal-network -p 3306:3306 -e MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD} -e MYSQL_DATABASE=${MYSQL_DATABASE} -e MYSQL_USER=${MYSQL_USERNAME} -e MYSQL_PASSWORD=${MYSQL_PASSWORD} -v mysql_data:/var/lib/mysql --restart unless-stopped mysql:8.0

# 3. Redis Exporter 실행 (모니터링)
docker run -d --name redis-exporter --network snackdeal-network -e REDIS_ADDR="redis://mite88Shop-redis:6379" -e REDIS_PASSWORD=${REDIS_PASSWORD:-} --restart unless-stopped oliver006/redis_exporter:latest

# 4. MySQL Exporter 실행 (모니터링)
docker run -d --name mysql-exporter --network snackdeal-network -e DATA_SOURCE_NAME="${MYSQL_USERNAME}:${MYSQL_PASSWORD}@(mite88Shop-mysql:3306)/" --restart unless-stopped prom/mysqld-exporter:latest

# 5. API 서버 실행 (포트 8080)
docker run -d --name mite88-app --network snackdeal-network -p 8080:8080 -e SPRING_PROFILES_ACTIVE=mysql,redis,prod -e MYSQL_HOST=mite88Shop-mysql -e MYSQL_PORT=3306 -e MYSQL_DATABASE=${MYSQL_DATABASE} -e MYSQL_USERNAME=${MYSQL_USERNAME} -e MYSQL_PASSWORD=${MYSQL_PASSWORD} -e REDIS_HOST=mite88Shop-redis -e REDIS_PORT=6379 -e REDIS_PASSWORD=${REDIS_PASSWORD:-} -e REDIS_TIMEOUT=3000ms -e REDIS_POOL_MAX_ACTIVE=10 -e REDIS_POOL_MAX_IDLE=5 -e REDIS_POOL_MIN_IDLE=2 -e JWT_APP_KEY=${JWT_APP_KEY} -e JWT_EXPIRATION=${JWT_EXPIRATION} -e JPA_DDL_AUTO=${JPA_DDL_AUTO} --restart unless-stopped ghcr.io/${GITHUB_REPOSITORY}:latest

# 6. Nginx Proxy Manager 실행 (프록시 / 포트 80, 443, 81)
docker run -d --name nginx-proxy-manager --network snackdeal-network -p 80:80 -p 443:443 -p 81:81 -v npm_data:/data -v npm_letsencrypt:/etc/letsencrypt --restart unless-stopped jc21/nginx-proxy-manager:latest

# 7. Prometheus 실행 (모니터링 / 포트 9090)
docker run -d --name prometheus --network snackdeal-network -p 9090:9090 -v $(pwd)/prometheus.yml:/etc/prometheus/prometheus.yml -v prometheus_data:/prometheus --restart unless-stopped prom/prometheus:latest

# 8. Grafana 실행 (시각화 대시보드 / 포트 3000)
docker run -d --name grafana --network snackdeal-network -p 3000:3000 -e GF_SECURITY_ADMIN_PASSWORD=${GRAFANA_ADMIN_PASSWORD} -v grafana_data:/var/lib/grafana --restart unless-stopped grafana/grafana:latest

# 9. Loki 실행 (로그 수집)
docker run -d --name loki --network snackdeal-network -v loki_data:/loki --restart unless-stopped grafana/loki:latest

# 10. Promtail 실행 (로그 포워더)
docker run -d --name promtail --network snackdeal-network -v /var/lib/docker/containers:/var/lib/docker/containers:ro -v /var/run/docker.sock:/var/run/docker.sock -v $(pwd)/promtail-config.yml:/etc/promtail/config.yml --restart unless-stopped grafana/promtail:latest -config.file=/etc/promtail/config.yml

```

### 3. 로컬 운영 인프라 컨테이너 리스트

| 컨테이너명 | 외부 포트 | 역할 및 비고 |
| --- | --- | --- |
| `mite88Shop-mysql` | `3306` | MySQL 8 데이터베이스 저장소 |
| `mite88Shop-redis` | 내부 전용 | Redis 7 세션 및 리프레시 토큰 저장소 |
| `mite88-app` | `8080` | **SnackDeal 백엔드 API 서버 본체** |
| `nginx-proxy-manager` | `80`, `443`, `81` | 리버스 프록시 / SSL 인증 / 관리 UI(81) |
| `mysql-exporter` | 내부 전용 | MySQL 상태 메트릭 수집기 |
| `redis-exporter` | 내부 전용 | Redis 상태 메트릭 수집기 |
| `prometheus` | `9090` | 메트릭 시계열 데이터베이스 수집 및 저장 |
| `grafana` | `3000` | 모니터링 시스템 시각화 대시보드 |
| `loki` | 내부 전용 | 컨테이너 로그 중앙 집중 관리 시스템 |
| `promtail` | 내부 전용 | 도커 로그를 수집하여 Loki로 전송하는 에이전트 |

---

### 필수 환경변수

| 변수 | 용도 |
| --- | --- |
| `JWT_APP_KEY` | JWT 서명용 HMAC 시크릿 키 |
| `JWT_EXPIRATION` / `JWT_REFRESH_EXPIRATION` | 액세스/리프레시 토큰 유효시간(ms) |
| `REDIS_HOST` / `REDIS_PORT` / `REDIS_PASSWORD` | Redis 연결 (리프레시 토큰/세션) |
| `MYSQL_HOST` / `MYSQL_PORT` / `MYSQL_DATABASE` / `MYSQL_USERNAME` / `MYSQL_PASSWORD` | MySQL 연결 |
| `GOOGLE_CLIENT_ID` / `GOOGLE_CLIENT_SECRET` | Google OAuth2 (선택) |
| `MAIL_HOST` / `MAIL_PORT` / `MAIL_USERNAME` / `MAIL_PASSWORD` | 이메일 인증코드 발송용 SMTP (기본값 gmail smtp.gmail.com:587) |
| `EMAIL_CODE_EXPIRATION` / `EMAIL_TOKEN_EXPIRATION` | 인증코드/인증토큰 유효시간(ms, 기본 5분/30분) |

Gmail 사용 시 `MAIL_USERNAME`은 발신 Gmail 주소, `MAIL_PASSWORD`는 Google 앱 비밀번호(App Password)를 사용해야 한다(일반 로그인 비밀번호 불가).

## 아키텍처 개요

### 패키지 구조

루트 패키지: `io.snackdeal.backand`. 도메인은 `entity / repository / mapper`를 공유하고, 사용자·관리자 API가 모두 있는 도메인은 `user/{controller,dto,service}`와 `admin/{controller,dto,service}`로 나눈다.

* `member` — 회원가입(이메일 인증 필수)·로그인·JWT·Google OAuth2·회원정보 관리. 관리자 회원관리는 `member/admin`.
* `admin` — 도메인에 속하지 않는 관리자 공통(로그인 `/admin/login`, 대시보드 `/admin/main`)
* `product` (+ `category`) — 상품/카테고리
* `cart` — 장바구니 (사용자 전용)
* `order` (+ `orders`/`order_item`/`shipping`/`payment` 엔티티) — 주문/결제/배송
* `delivery` — 배송지 주소록 (사용자 전용)
* `coupon` (+ `coupon_board`/`user_coupon`) — 쿠폰/이벤트게시판/쿠폰함
* `cs` — 공지/FAQ/문의(QNA)/QNA답변
* `global` — 공통 응답(`CommonResponse`), 응답코드(`ResponseCode`), 예외 처리(`BusinessException`/`GlobalExceptionHandler`), Redis 설정, Swagger 설정

### 구현 범위 (이번 스캐폴딩 기준)

* **완전 구현**: `member` 도메인 전체 — 이메일 인증코드 발송/검증(`/member/email/send-code`, `/member/email/verify-code`), 회원가입(`/member/join`), 로그인/로그아웃/토큰갱신(`/member/login`, `/member/logout`, `/member/token/refresh`), 내정보 조회/수정(`/member/me`), Google OAuth2 로그인, 관리자 로그인(`/admin/login`)과 회원관리(`/admin/members`).
* **골격만 구현**: `product`/`cart`/`order`/`delivery`/`coupon`/`cs`/`admin` 대시보드 — 엔티티·리포지토리·컨트롤러(요청 경로만 확정)·서비스(전부 `NOT_IMPLEMENTED` 예외)까지만 존재. 실제 비즈니스 로직은 추후 구현 필요.

### 인증 흐름

JWT + Redis 세션(중복 로그인 방지), 이메일을 로그인 식별자로 사용(별도 username 없음).

1. `TokenAuthenticationFilter`가 `Authorization: Bearer <token>`을 검증하고 `sid` 클레임을 Redis `session:{email}` 값과 비교.
2. 로그인 성공 시 `AuthService.issueTokens`가 액세스·리프레시 토큰 발급, 리프레시 토큰은 `RefreshTokenService`가 Redis에 저장.
3. Google OAuth2는 `member` 테이블에 `provider_id` 컬럼이 없어(스키마 고정) **이메일 기준으로만** 매칭한다. 최초 로그인 시 자동가입되며, 스키마상 NOT NULL인 `birth`/`gender`/`phone`은 임시값(2000-01-01/MALE/빈 문자열)으로 채운다 — 프론트에서 최초 로그인 후 추가 정보 입력 화면으로 보완하는 것을 전제로 한 임시 조치이니 실제 서비스 전에 검토 필요.
4. **Google OAuth2 로그인 설정**:
   * `application-google.yml` 프로파일을 활성화해야 합니다 (`SPRING_PROFILES_ACTIVE` 환경변수에 `google` 추가).
   * Google Cloud Console에서 OAuth 2.0 클라이언트 ID를 생성하고, 발급받은 `클라이언트 ID`와 `클라이언트 보안 비밀`을 각각 `GOOGLE_CLIENT_ID`와 `GOOGLE_CLIENT_SECRET` 환경변수로 설정해야 합니다.
   * 리다이렉트 URI는 `http://localhost:8080/oauth2/authorization/google` (개발 환경 기준)으로 설정해야 합니다.
   * Google OAuth2는 `member` 테이블에 `provider_id` 컬럼이 없어(스키마 고정) **이메일 기준으로만** 매칭합니다. 최초 로그인 시 자동가입되며, 스키마상 NOT NULL인 `birth`/`gender`/`phone`은 임시값(2000-01-01/MALE/빈 문자열)으로 채워집니다. 프론트엔드에서 최초 로그인 후 추가 정보 입력 화면으로 보완하는 것을 전제로 한 임시 조치이니 실제 서비스 전에 검토가 필요합니다.
5. 관리자 로그인(`/admin/login`)은 일반 로그인과 동일한 검증을 거치되 `role == ADMIN`이 아니면 거부한다.

### 데이터베이스 마이그레이션

Flyway가 `src/main/resources/db/migration/`의 스크립트로 스키마 관리. V1~V18이 테이블 생성(회원→이메일인증→카테고리→상품→상품이미지→장바구니→쿠폰게시판→쿠폰→쿠폰함→주문→주문상품→배송→결제→배송지→공지→FAQ→QNA→QNA답변 순, FK 의존 순서), V19~V20이 샘플 데이터 삽입. 스키마 변경 시 기존 파일 수정 금지, 다음 번호 파일 추가.

**샘플 계정**: `admin@snackdeal.io` / `admin1234` (ADMIN), `user@snackdeal.io` / `user1234` (USER).

## 참고 사항 및 확인 필요 항목

* `mite88_shop`의 AI 작업 큐(`AiJob`/`JobService`/`AiJobWorker`)와 Thymeleaf 뷰는 이번 프로젝트에 옮기지 않았다(프론트가 React라 뷰 불필요, AI 큐는 SnackDeal 요구사항에 없음). 필요해지면 별도로 추가.
* 관리자 대시보드(`GET /admin/main`)는 실제 집계 로직이 없는 고정값(0) 응답 스텁이다.
* 결제(포트원→토스페이먼츠)·재고 차감·쿠폰 검증 등 도메인 로직은 아직 없다. `order`/`coupon` 서비스 메서드는 모두 `NOT_IMPLEMENTED`를 던진다.

```

```