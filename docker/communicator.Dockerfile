FROM gradle:7.4.2-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle :communicator:communicator-app:bootJar --no-daemon

FROM openjdk:18.0.1.1-slim
EXPOSE 8083 8084
RUN mkdir /app
COPY ./docker/communicator/ /app
COPY ./docker/logback-spring.xml /app/logback-spring.xml
COPY --from=build /home/gradle/src/communicator/communicator-app/build/libs/communicator-app-1.0-SNAPSHOT.jar /app/communicator.jar
WORKDIR /app
CMD ["java", "-jar", "communicator.jar"]
