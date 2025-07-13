FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY target/market-data-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "app.jar"]