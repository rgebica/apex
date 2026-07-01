# Apex

![CI](https://github.com/rgebica/apex/actions/workflows/ci.yml/badge.svg)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-brightgreen)
![License](https://img.shields.io/badge/license-MIT-blue)

A community platform for motorcyclists to discover, create, and share the twistiest, most scenic riding routes. Instead of optimising for the fastest way from A to B, Apex answers the question a regular map never does: **how twisty and worth riding is this road?**

Every route carries a **curviness score** — a single number telling a rider how much fun a road actually is. That's the hook the whole product is built around.

> **Status:** Phase 0 — foundation (a modular monolith with clean DDD context boundaries). Later phases are tracked in the roadmap below.

## Why this project

Apex is a portfolio project built with a deliberate architectural agenda. Each feature is an excuse to practise a specific skill in a setting where it's genuinely justified, not bolted on:

- **DDD** — the domain (routes, rides, ratings) splits cleanly into bounded contexts with real aggregates and invariants.
- **Event-driven architecture** — heavy work (route geometry analysis, image processing, weather lookups) runs asynchronously behind a message broker.
- **Docker & Kubernetes** — services are containerised and orchestrated, with workers that autoscale on queue depth.
- **Microservices** — extracted along the context seams designed up front in the monolith.

The guiding principle: start as a modular monolith with clean boundaries, and only add distributed complexity where it earns its place.

## Tech stack

**Now (Phase 0):** Java 21 · Spring Boot 3.3 · Hibernate/JPA · PostgreSQL · Liquibase · Maven · Docker

**Planned:** RabbitMQ (message broker) · Keycloak (auth) · MinIO (photo storage) · PostGIS (geo search) · Kubernetes / k3s + KEDA · Flutter (cross-platform mobile app)

## Architecture

The app is a single deployable artifact, internally split into bounded contexts. Module boundaries today become service boundaries tomorrow — which makes extraction mechanical rather than a rewrite.

| Area | Context | Owns |
|------|---------|------|
| Core | `routing` | creating, editing, publishing routes |
| Core | `analysis` | curviness score, elevation profile, length |
| Support | `weather` | conditions for the planned ride time |

Each module follows a ports & adapters layout — dependencies point inward toward a framework-free domain.

## Project structure

```
src/main/java/pl/apex/
├── routing/
│   ├── domain/         # Route aggregate, value objects, business rules
│   ├── application/    # use cases (thin, transaction boundary)
│   └── infrastructure/ # adapters: JPA, REST, external APIs
├── analysis/
└── weather/
```

## Getting started

Requirements: JDK 21, Docker.

```bash
# 1. start the database
docker-compose up -d postgres

# 2. run the app
./mvnw spring-boot:run

# run tests
./mvnw test
```

## Roadmap

Built in layers — every phase ends with something that works.

| Phase | Focus | Skill |
|-------|-------|-------|
| 0 | Modular monolith, Route domain | DDD |
| 1 | Dockerfile + compose | Docker |
| 2 | RabbitMQ + outbox pattern | Message broker |
| 3 | Service extraction, API gateway | Microservices |
| 4 | Keycloak, catalog, ratings, photos | Community features |
| 5 | k3s + KEDA autoscaling | Kubernetes |

## License

MIT — see [LICENSE](LICENSE).
