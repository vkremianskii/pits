FROM gradle:7.4.2-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle :processes:bootJar --no-daemon

FROM openjdk:18.0.1.1-slim
EXPOSE 8082
RUN mkdir /app
COPY ./docker/processes/ /app
COPY --from=build /home/gradle/src/processes/build/libs/processes-1.0-SNAPSHOT.jar /app/processes.jar
WORKDIR /app
CMD ["java", "-jar", "processes.jar"]
