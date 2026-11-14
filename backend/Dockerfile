# 빌드
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# 의존성만 먼저 받아 레이어 캐시 활용 (pom.xml이 바뀔 때만 다시 다운로드)
COPY pom.xml .
RUN mvn -q -B dependency:go-offline

# 소스 복사 후 실행 jar 생성 (테스트는 CI/로컬에서 수행)
COPY src ./src
RUN mvn -q -B package -DskipTests && cp target/*.jar app.jar

# 실행 (JRE 21만 포함)
FROM eclipse-temurin:21-jre
WORKDIR /app

# root가 아닌 전용 사용자로 실행
RUN groupadd --system app && useradd --system --gid app app
COPY --from=build /app/app.jar app.jar
USER app

# Render는 PORT 환경변수로 포트를 지정 (application.yml에서 SERVER_PORT > PORT > 8080 순으로 사용)
EXPOSE 8080

# 컨테이너 메모리 한도에 맞춰 힙 크기 자동 조정 (Render 무료 인스턴스 512MB 대비)
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-XX:+UseSerialGC", "-jar", "app.jar"]
