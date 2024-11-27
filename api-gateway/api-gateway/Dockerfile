FROM maven:3.9.8-amazoncorretto-21-debian AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn package -DskipTests

FROM amazoncorretto:21.0.5

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 9000

ENTRYPOINT ["java", "-jar", "app.jar"]