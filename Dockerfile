# ====================== BUILD STAGE ======================
FROM gradle:8.5-jdk21 AS build

WORKDIR /app

# Copy Gradle wrapper & config first to cache dependencies
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Make gradlew executable
RUN chmod +x gradlew

# Download dependencies only to cache layer
RUN ./gradlew build -x test --parallel --no-daemon || true

# Copy source code
COPY src src

# Build the Spring Boot jar
RUN ./gradlew bootJar -x test --parallel --no-daemon

# ====================== RUNTIME STAGE ======================
FROM eclipse-temurin:21-jre-jammy AS runtime

WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Set Spring profile
ENV SPRING_PROFILES_ACTIVE=production

# Expose app port
EXPOSE 8080

# Use tini to handle PID 1 properly (prevents zombie processes)
RUN apt-get update && apt-get install -y tini && rm -rf /var/lib/apt/lists/*

ENTRYPOINT ["tini", "--", "java", "-jar", "app.jar"]
