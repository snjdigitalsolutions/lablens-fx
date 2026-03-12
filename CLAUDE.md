# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Build
./mvnw clean package

# Run the application
./mvnw spring-boot:run

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=LablensFxApplicationTests

# Run a single test method
./mvnw test -Dtest=LablensFxApplicationTests#contextLoads
```

## Architecture Overview

**lablens-fx** is a module within the `com.snjdigitalsolutions:lablens` parent Maven project. It is a **JavaFX desktop application** backed by **Spring Boot**, using an embedded **H2 file-based database** with **Flyway** migrations.

### Key architectural decisions

- **Spring Boot + JavaFX integration**: The app boots via `SpringApplication.run()` in `LablensFxApplication`. JavaFX UI components are intended to be Spring-managed beans, allowing dependency injection into controllers/nodes.
- **Custom FXML component pattern**: UI components extend JavaFX layout classes (e.g., `RootPane extends BorderPane`) and load their own FXML via `fx:root` — this is the custom component pattern, not the standard controller pattern. The FXML file is named after and co-located with its Java class under `src/main/resources/fxml/`.
- **H2 file-based persistence**: Data is stored at `./data/lablens` (relative to working directory). The dev profile enables the H2 console at `http://localhost:8080/h2-console`.
- **Flyway migrations**: SQL migrations live in `src/main/resources/db/migration/` following `V{n}__{description}.sql` naming. DDL is managed exclusively by Flyway (`spring.jpa.hibernate.ddl-auto=none`).

### Domain model

The core entity is `compute_resource` — a monitored host/server with fields: `id`, `ipaddress`, `os`, `description`, `hostname`.

### Current UI structure

`RootPane` is the top-level UI component with a nav bar containing: Dashboard, Configs, Logs, Timeline views, plus Add Host and Settings actions. The center content area is currently empty (stubbing in progress).

## Dev Profile

Run with `-Dspring.profiles.active=dev` to enable the H2 web console at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:file:./data/lablens`, user: `sa`, no password).
