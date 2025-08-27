# ====================== BUILD STAGE ======================
FROM gradle:8.5-jdk21 AS build

WORKDIR /app

# Copy Gradle wrapper & config first (cache deps)
COPY gradlew . 
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x gradlew

# Pre-download dependencies (cache)
RUN ./gradlew dependencies --no-daemon || true

# Copy source
COPY src src

# Build the Spring Boot fat jar (skip tests)
RUN ./gradlew bootJar -x test --no-daemon

# ====================== RUNTIME STAGE ======================
FROM eclipse-temurin:21-jre-jammy AS runtime

WORKDIR /app

# Copy built jar
COPY --from=build /app/build/libs/*.jar app.jar

# Profile
ENV SPRING_PROFILES_ACTIVE=production

ENV JAVA_OPTS="-Xmx256m -Xms128m -XX:+UseSerialGC -XX:+UseContainerSupport"

EXPOSE 8080

RUN apt-get update && apt-get install -y --no-install-recommends tini \
    && rm -rf /var/lib/apt/lists/*

ENTRYPOINT ["tini", "--"]
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
