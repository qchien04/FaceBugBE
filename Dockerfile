# Build stage
FROM maven:3.9.8-eclipse-temurin-22 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:22-jdk-alpine

# Install FFmpeg and other dependencies
RUN apk add --no-cache ffmpeg

WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Create upload/output folders
RUN mkdir -p /app/uploads /app/streams

# Render will set PORT via env var — your Spring Boot must honor this
ENV PORT 8080
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
