FROM openjdk:17-slim

WORKDIR /app

COPY pom.xml .
COPY src src
RUN apt-get update && apt-get install -y maven && mvn clean package

COPY target/movieuz_bot-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar

CMD ["java", "-jar", "app.jar"]
