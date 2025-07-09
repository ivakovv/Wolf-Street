FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY target/matching-engine-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8087
ENTRYPOINT ["java", "-jar", "app.jar"]