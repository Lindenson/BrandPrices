# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN apk add --no-cache curl
RUN addgroup -S spring && adduser -S spring -G spring
RUN mkdir -p /app/logs && chown -R spring:spring /app/logs

USER spring:spring

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+HeapDumpOnOutOfMemoryError -Djava.security.egd=file:/dev/urandom"

HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/prices/final?date=2020-06-14T10:00:00&productId=35455&brandId=1 || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
