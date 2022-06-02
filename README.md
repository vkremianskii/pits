# pits

![gradle workflow](https://github.com/vkremianskii/pits/actions/workflows/gradle.yml/badge.svg)

Open-pit mining system – exercise in software engineering.

**Disclaimer**: pits is not intended for use in production. It is a personal project with a goal to practice and showcase my skills.

## Business Processes

### Haul Cycles

![State diagram](doc/haul-cycles.jpg)

#### Implementation

- Changes in equipment position and truck payload are persisted to *processes* database in real-time
- Every N seconds, *processes* runs [HaulCycleJob](processes/src/main/java/com/github/vkremianskii/pits/processes/job/HaulCycleJob.java), which computes haul cycles for every truck
- [HaulCycleService](processes/src/main/java/com/github/vkremianskii/pits/processes/logic/HaulCycleService.java) launches [HaulCycleFsm](processes/src/main/java/com/github/vkremianskii/pits/processes/logic/fsm/HaulCycleFsm.java) (a finite-state machine), and replays persisted events through it, starting from the latest haul cycle
- After truck haul cycles are computed, they are persisted to *processes* database

## Architecture

![Component diagram](doc/components.jpg)

|Service|Purpose|Implemented|Dependencies|
|-|-|-|-|
|Auth|- Manages and authenticates users|:heavy_check_mark:|- PostgreSQL|
|Registry|- Manages equipment and locations|:heavy_check_mark:|- PostgreSQL<br>- RabbitMQ<br>- Auth|
|Processes|- Tracks historical data (e.g., equipment positions)<br>- Supervises business processes (e.g., haul cycles)|:heavy_check_mark:|- PostgreSQL<br>- RabbitMQ<br>- Registry<br>- Auth|
|Communicator|- Talks to mobile equipment (e.g., trucks)|:heavy_check_mark:|- RabbitMQ|
|Web App|- UI for mine personnel|:x:|- Registry<br>- Processes|
|Mobile Equipment|- On-board computer software|:x:|- Communicator|

## Technologies

### Backend

- Java 17
- Spring Boot
- Spring WebFlux
- Spring Security
- jOOQ
- Flyway
- gRPC
- Gradle

### Infra

- Docker
- PostgreSQL
- RabbitMQ
- MongoDB

### Other

- Swing
- JMapViewer

## Installation

### Requirements

- Docker

### Commands

- Start infra services: `docker-compose -f docker/docker-compose.yml up -d db adminer rabbitmq`
- Build backend services: `docker-compose -f docker/docker-compose.yml build auth registry processes communicator`
- Start backend services: `docker-compose -f docker/docker-compose.yml up -d auth registry processes communicator`
- Optionally, start MongoDB: `docker-compose -f docker/docker-compose.yml up -d mongo mongo-express`

### Services

|Service|Port|Protocol|
|-|-|-|
|Auth|8080|HTTP|
|Registry|8081|HTTP|
|Processes|8082|HTTP|
|Communicator|8083,8084|HTTP,gRPC|
|Adminer|8090|HTTP|
|PostgreSQL|5432|TCP|
|RabbitMQ|5672,15672|AMQP,HTTP|
|MongoDB|27017|TCP
|Mongo Express|8091|HTTP
