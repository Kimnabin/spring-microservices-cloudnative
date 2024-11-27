FROM maven:3.9.8-amazoncorretto-21-debian AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src
COPY docker-compose.yml .

RUN mvn package -DskipTests

FROM amazoncorretto:21.0.5

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
