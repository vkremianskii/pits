FROM gradle:7.4.2-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle :registry:registry-app:bootJar --no-daemon

FROM openjdk:18.0.1.1-slim
EXPOSE 8081
RUN mkdir /app
COPY ./docker/registry/ /app
COPY ./docker/logback-spring.xml /app/logback-spring.xml
COPY --from=build /home/gradle/src/registry/registry-app/build/libs/registry-app-1.0-SNAPSHOT.jar /app/registry.jar
WORKDIR /app
CMD ["java", "-jar", "registry.jar"]
