# syntax=docker/dockerfile:1

# ---- Etap 1: budowanie (build stage) ----
# Pelne JDK 21 + Maven - potrzebne tylko do zbudowania aplikacji.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

# Najpierw sam pom.xml + pobranie zaleznosci. To osobna warstwa, ktora
# Docker cache'uje: dopoki pom.xml sie nie zmienia, przy kolejnych buildach
# nie sciaga zaleznosci od nowa.
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Teraz kod zrodlowy i wlasciwy build. Testy pomijamy tutaj, bo uruchamia je
# CI (.github/workflows/ci.yml) - obraz ma tylko zbudowac gotowy artefakt.
COPY src ./src
RUN mvn -B clean package -DskipTests

# ---- Etap 2: uruchomienie (runtime stage) ----
# Samo JRE 21 (bez Mavena, bez kompilatora) - mniejsze i bezpieczniejsze pudelko.
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

# Przenosimy z etapu build TYLKO gotowy, wykonywalny JAR. Kod zrodlowy i Maven
# zostaja w etapie build i nie trafiaja do finalnego obrazu.
COPY --from=build /build/target/apex-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
