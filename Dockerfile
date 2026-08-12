# syntax=docker/dockerfile:1

FROM maven:3.9.11-eclipse-temurin-17-alpine AS build

WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw -B -DskipTests dependency:go-offline

COPY src/ src/
RUN ./mvnw -B -DskipTests package

FROM eclipse-temurin:17-jre-alpine AS runtime

RUN addgroup -S pdfwizard \
    && adduser -S -G pdfwizard pdfwizard

WORKDIR /app

COPY --from=build --chown=pdfwizard:pdfwizard \
    /workspace/target/pdf-wizard-0.0.1-SNAPSHOT.jar \
    /app/pdf-wizard.jar

USER pdfwizard

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/pdf-wizard.jar"]
