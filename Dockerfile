FROM gradle:8.7.0-jdk21 AS builder

WORKDIR /app

# gradle setup
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew ./
COPY /gradle/wrapper/gradle-wrapper.properties ./gradle/wrapper

# clean old build
RUN ./gradlew clean

# copy project src
COPY src ./src

# build new jar
RUN ./gradlew shadowJar

# lightweight runtime image
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/build/libs/order-ingestion.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]