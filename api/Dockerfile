# ── Stage 1: Build ─────────────────────────────
FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /api

# cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# build source
COPY src ./src
RUN mvn package -DskipTests -B


# ── Stage 2: Run ───────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /api

COPY --from=build /api/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]