# Apex

Platforma społecznościowa dla motocyklistów — do odkrywania, tworzenia i udostępniania krętych, widokowych tras. Zamiast optymalizować czas dojazdu, Apex odpowiada na pytanie, którego nie zadaje zwykła nawigacja: **jak bardzo kręta i warta przejechania jest ta droga.**

> Status: **Faza 0 — fundament** (modularny monolit z czystymi granicami kontekstów DDD). Kolejne fazy w `roadmap-moto-community.md`.

## Stack

- Java 21, Spring Boot 3.3
- Hibernate/JPA, PostgreSQL, Liquibase
- Maven
- Docker (Postgres lokalnie)

W kolejnych fazach: RabbitMQ (broker), Keycloak (auth), MinIO (zdjęcia), Kubernetes (k3s + KEDA), PostGIS (wyszukiwanie geo), Angular na froncie.

## Struktura — modularny monolit

Aplikacja jest jednym deployowalnym artefaktem, ale w środku podzielona na bounded contexty. Granice modułów dziś = granice serwisów w przyszłości.

```
src/main/java/pl/apex/
├── routing/        # tworzenie, edycja, publikacja tras
│   ├── domain/         # agregat Route, value objecty, reguly biznesowe
│   ├── application/    # use case'y (cienkie, granica transakcji)
│   └── infrastructure/ # adaptery: JPA, REST, zewnetrzne API
├── analysis/       # krętość, profil wysokości, długość
└── weather/        # warunki na planowanej trasie
```

Każdy moduł trzyma się układu ports & adapters: zależności wskazują do środka, na domenę. Domena jest czysta (bez Springa, bez JPA).

## Jak uruchomić

Wymagane: JDK 21, Docker.

```bash
# 1. baza danych
docker-compose up -d postgres

# 2. aplikacja
./mvnw spring-boot:run
```

Testy:

```bash
./mvnw test
```

## Roadmap

Projekt jest budowany warstwami — po każdej fazie jest coś działającego:

| Faza | Cel | Domknięta luka |
|------|-----|----------------|
| 0 | Modularny monolit, domena Route | DDD |
| 1 | Dockerfile + compose | Docker |
| 2 | RabbitMQ + outbox | Message broker |
| 3 | Wydzielenie serwisów, API gateway | Mikroserwisy |
| 4 | Keycloak, katalog, oceny, zdjęcia | społeczność |
| 5 | k3s + KEDA autoscaling | Kubernetes |

## Licencja

MIT — patrz `LICENSE`.
