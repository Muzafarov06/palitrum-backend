FROM gradle:8.13-jdk21 AS build
WORKDIR /app

# Копируем только папку с проектом (где лежит build.gradle)
COPY palitrum /app

RUN gradle build -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
