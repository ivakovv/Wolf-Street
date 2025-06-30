FROM maven:3.9.5-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml ./parent-pom.xml
RUN mvn install:install-file -Dfile=parent-pom.xml -DgroupId=com.wolfstreet -DartifactId=trading-platform -Dversion=1.0.0-SNAPSHOT -Dpackaging=pom

COPY user-service/pom.xml ./pom.xml
COPY user-service/src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]