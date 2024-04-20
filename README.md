![Bot](https://github.com/AlexCawl/java-course-2023-backend-project/actions/workflows/bot.yml/badge.svg)
![Scrapper](https://github.com/AlexCawl/java-course-2023-backend-project/actions/workflows/scrapper.yml/badge.svg)

# Link Tracker

> Made by **Alexcawl** (Mikhail Babushkin)

> Telegram: [@fantasmagorius](https://t.me/fantasmagorius)

An application for tracking content updates via links.
When new events occur, a notification is sent to the Telegram.

The project consists of 2 applications:

* Bot
* Scrapper

## Tech-stack

### Core

* `Java 21`
* `Spring Boot 3`

### Persistence

Depends on the configuration. Both methods have been implemented. 

Also `PostgreSQL` is used as the database.

* `JDBC client`
* `Spring Data JPA`

### Service communication

* `REST API`
* `Apacha Kafka` (Optional)

### Metrics

* `Grafana | Prometheus`
* `Spring Boot Actuator`
* `Micrometer`

### CI/CD

* `GitHub Actions`
* `Docker | Docker-compose`
