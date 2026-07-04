# SnackDeal Backend — AI 작업 규칙

> 이 파일은 매 세션 자동 로드됩니다. **짧게 유지하세요.** 상세 절차는 `.claude/skills/`에, API 스펙은 `프로젝트 기능/`에 있습니다. 여기에 복붙하지 마세요.

## 스택
- Spring Boot 4.1 / Java 25, Gradle
- JPA + Flyway(MySQL 운영 / H2 로컬), Redis
- Spring Security + OAuth2 + JWT(jjwt)
- springdoc-openapi(Swagger), Actuator + Prometheus

## 아키텍처 (계층형)
- `api/{admin,user}/{도메인}/controller,dto` — 요청/응답 (프레젠테이션)
- `domain/{도메인}/entity,repository,service` — 비즈니스 로직
- `global/config,exception,mail` — 공통/횡단 관심사
- 컨트롤러는 얇게, 로직은 service에. DTO ↔ Entity 변환은 mapper 또는 service에서.

## API 스펙 위치
- 기능별 명세: `프로젝트 기능/기능명세서 API 명세서/API 정의서/*.md`
- **특정 기능 작업 시 해당 도메인 md만 읽으세요.** 전체를 한 번에 읽지 마세요 (토큰 낭비).
- DB 스키마 변경은 반드시 `src/main/resources/db/migration/V{n}__*.sql` 새 파일로. 기존 마이그레이션 수정 금지.

## 작업 흐름
1. 관련 기능 명세 md와 대상 도메인 코드만 읽는다.
2. 기존 도메인의 패턴(네이밍·예외처리·응답 포맷)을 그대로 따른다.
3. 컨트롤러 추가 시 대응 service·repository·dto도 같은 도메인 패키지에.
4. 변경 후 `scripts/verify.ps1` 실행(또는 `./gradlew test`).

## 금지
- 기존 Flyway 마이그레이션 파일 수정 (새 버전으로만 추가)
- 시크릿/키를 코드·yml에 하드코딩 (환경변수 사용)
- 요청 없이 대량 리팩터링·의존성 추가

## 상세 절차 (필요할 때만 읽기)
- 신규 API 엔드포인트 추가 → `.claude/skills/spring-api/SKILL.md`
- 에러 기록 → `.claude/skills/collect-error/SKILL.md`
