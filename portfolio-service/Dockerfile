FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY target/portfolio-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8083 6565
ENTRYPOINT ["java", "-jar", "app.jar"]