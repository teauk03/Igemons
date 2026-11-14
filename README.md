# Igemono (이게모노)

- **Igemono(이게모노)**는 파충류·양서류·절지류 등 특수동물을 사랑하는 집사들을 위한 **All-In-One 케어 및 분양 통합 플랫폼**입니다.
- 개체별 급여·탈피 기록을 관리하는 **디지털 건강수첩**, 간편하게 작성하는 **원클릭 양도·양수 계약서**, 신뢰 기반의 **분양 마켓**, 그리고 집사들의 노하우를 나누는 **커뮤니티** 기능을 제공합니다.
- 프로젝트는 독립적으로 실행 가능한 Next.js 프론트엔드와 Spring Boot 백엔드로 구성된 모노레포(Monorepo) 구조를 갖추고 있습니다.

```text
frontend/  Next.js, React, TypeScript, Tailwind CSS
backend/   Java 21, Spring Boot, Maven, PostgreSQL
```

---

## Frontend (프론트엔드)

**필수 요구사항:** Node.js, npm

```bash
cd frontend
npm ci
npm run dev
```

- 실행 후 브라우저에서 <http://localhost:3000> 으로 접속할 수 있습니다.
- 코드 린트 및 빌드 검증이 필요할 때는 `frontend/` 경로에서 `npm run lint` 또는 `npm run build`를 실행하세요.
- 백엔드 서버와 연동하려면 `frontend/.env.local` 파일에 `NEXT_PUBLIC_API_BASE_URL` 값을 설정해야 합니다. (기본 예제 파일은 `http://localhost:8080`을 가리킵니다.)

---

## Backend (백엔드)

**필수 요구사항:** Java 21, Maven, Docker 및 Docker Compose

```bash
cd backend
cp .env.example .env
docker compose up -d
set -a && . ./.env && set +a
mvn spring-boot:run
```

- Spring Boot 애플리케이션은 <http://localhost:8080> 포트에서 실행되며, 초기 상태 확인용 헬스 체크 엔드포인트는 `GET /api/health`입니다.
- 백엔드는 `FRONTEND_URL` 환경변수에 지정된 프론트엔드 출처(Origin)의 브라우저 CORS 요청을 허용합니다. (기본값: `http://localhost:3000`)
- Docker Compose는 `5432` 포트에 PostgreSQL을 실행하며, 데이터는 지정된 볼륨(Named Volume)에 영구 저장됩니다.
  - `.env.example`에 포함된 계정 정보는 로컬 개발 전용이므로, 공용 또는 배포 환경에 적용하기 전 반드시 변경하세요.
  - 데이터베이스를 종료할 때는 `docker compose down`을 사용하며, 저장된 데이터까지 의도적으로 초기화할 때만 `-v` 옵션을 추가하세요.

### 백엔드 아키텍처 (Hexagonal Architecture)

백엔드는 도메인 중심의 헥사고날 아키텍처(포트-어댑터 패턴) 구조로 설계되어 있습니다.

```text
backend/src/main/java/com/igemons/
├── domain/                   # 핵심 비즈니스 개념 및 도메인 규칙
├── application/
│   ├── port/in/              # 유스케이스 입력 포트 (인터페이스)
│   └── service/              # 유스케이스 구현체
└── adapter/
    ├── config/               # 유스케이스 및 프레임워크 빈(Bean) 설정
    └── in/web/               # HTTP 컨트롤러