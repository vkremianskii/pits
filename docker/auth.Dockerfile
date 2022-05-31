FROM gradle:7.4.2-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle :auth:auth-app:bootJar --no-daemon

FROM openjdk:18.0.1.1-slim
EXPOSE 8080
RUN mkdir /app
COPY ./docker/auth/ /app
COPY --from=build /home/gradle/src/auth/auth-app/build/libs/auth-app-1.0-SNAPSHOT.jar /app/auth.jar
WORKDIR /app
CMD ["java", "-jar", "auth.jar"]
