# pits

Open-pit mining system – exercise in software engineering

## Technologies

- Java 17
- Spring Boot 2
- jOOQ
- Flyway
- gRPC
- Gradle
- PostgreSQL
- RabbitMQ
- Docker

## Installation

`docker-compose -f docker/docker-compose.yml up -d`

## Services

|Name|Port|
|-|-|
|Registry|8080|
|Communicator - HTTP|8081|
|Communicator - GRPC|8082|
|Processes|8083|
|PostgreSQL|5432|
|RabbitMQ|5672,15672|
|Adminer|8090|
