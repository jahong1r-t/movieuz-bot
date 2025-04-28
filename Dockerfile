# 1-stage: Build
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src src

RUN mvn clean package

# 2-stage: Run
FROM openjdk:17-slim

WORKDIR /app

COPY --from=build /app/target/movieuz_bot-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar

CMD ["java", "-jar", "app.jar"]
