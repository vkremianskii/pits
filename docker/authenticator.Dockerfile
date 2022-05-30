FROM gradle:7.4.2-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle :authenticator:authenticator-app:bootJar --no-daemon

FROM openjdk:18.0.1.1-slim
EXPOSE 8080
RUN mkdir /app
COPY ./docker/authenticator/ /app
COPY --from=build /home/gradle/src/authenticator/authenticator-app/build/libs/authenticator-app-1.0-SNAPSHOT.jar /app/authenticator.jar
WORKDIR /app
CMD ["java", "-jar", "authenticator.jar"]
