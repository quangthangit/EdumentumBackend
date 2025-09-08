# ====================== BUILD STAGE ======================
FROM gradle:8.5-jdk21 AS build

WORKDIR /app
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon || true
COPY src src
RUN ./gradlew bootJar -x test --no-daemon

# ====================== RUNTIME STAGE ======================
FROM eclipse-temurin:21-jre-jammy AS runtime

WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

# Profile
ENV SPRING_PROFILES_ACTIVE=production
ENV JAVA_OPTS="-Xmx256m -Xms128m -XX:+UseSerialGC -XX:+UseContainerSupport"

EXPOSE 8080

# Download tini binary instead of apt-get
ADD https://github.com/krallin/tini/releases/download/v0.19.0/tini /tini
RUN chmod +x /tini

ENTRYPOINT ["/tini", "--"]
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
