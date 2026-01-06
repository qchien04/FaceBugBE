FROM eclipse-temurin:22-jdk-alpine

RUN apk add --no-cache ffmpeg

WORKDIR /app

# Copy prebuilt jar
COPY target/*.jar app.jar

RUN mkdir -p /app/uploads /app/streams

ENV PORT 8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
