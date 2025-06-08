FROM eclipse-temurin:21-jre

WORKDIR /app

COPY build/libs/order-ingestion.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]