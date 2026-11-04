# CQRS + Event Sourcing Demo (Axon Framework)

A demo of **CQRS** (Command Query Responsibility Segregation) and **event sourcing** built with Spring Boot microservices and [Axon Framework](https://www.axoniq.io/axon-framework). Writes go through a **command** service that stores domain events in Axon Server. A **query** service builds its own read models from those events and serves reads. A **Eureka** discovery server and a **Spring Cloud Gateway** sit in front of them.

The project is a learning and demo codebase. It is not production-ready. See [Known limitations](#known-limitations).

---

## Table of contents

- [Architecture](#architecture)
- [Modules](#modules)
- [Project structure](#project-structure)
- [Tech stack](#tech-stack)
- [REST API](#rest-api)
- [Domain events and commands](#domain-events-and-commands)
- [Data model](#data-model)
- [Configuration reference](#configuration-reference)
- [Running locally](#running-locally)
- [Trying it out](#trying-it-out)
- [Build notes](#build-notes)
- [Known limitations](#known-limitations)

---

## Architecture

```
                   HTTP
  client ───────────────────────►  api-gateway :8080  (Spring Cloud Gateway)
                                       │  routes /{service-id}/** via Eureka
                  ┌────────────────────┴────────────────────┐
                  ▼                                         ▼
  ┌────────────────────────────────┐        ┌────────────────────────────────┐
  │ command  (random port)         │        │ query  (random port)           │
  │ REST  POST /api/store          │        │ REST  GET /api/store/{id}      │
  │       PATCH /api/store         │        │       GET /api/gadget/...      │
  │ Axon aggregate StoreAggregate  │        │ Read models (H2 "query")       │
  │ H2 "command" (store, gadget)   │        │ Axon query handlers            │
  └───────────────┬────────────────┘        └───────────────▲────────────────┘
                  │ commands / events                        │ events (tracking processor)
                  ▼                                          │
         ┌────────────────────────────────────────────────────────┐
         │              Axon Server  (localhost:8124)             │
         │        event store + command/query message routing     │
         └────────────────────────────────────────────────────────┘

  discovery-server :8761  (Eureka) ◄── command, query and api-gateway register here
```

Key design points:

- **Commands and queries are separated.** The `command` service accepts writes and emits events. The `query` service never receives commands and only reads its own tables.
- **Event sourcing on the write side.** `StoreAggregate` is rebuilt from its events. Its state is not stored directly. Events are the source of truth and live in Axon Server.
- **Read models are projections.** The `query` service turns each event into denormalised tables (store, gadget, store-to-gadget, gadget-to-country) optimised for the queries it serves.
- **Service discovery.** `command`, `query` and `api-gateway` register with Eureka. Both business services use a random port, so you find them through the gateway or Eureka rather than a fixed port.

---

## Modules

The project is a Maven multi-module build. The parent POM is `pom.xml` at the root.

| Module | Port | Role | Persistence | Axon role |
|---|---|---|---|---|
| `dto-event` | – | Shared event contracts (`StoreCreatedEvent`, `StoreNameUpdatedEvent`, `GadgetState`). Library, no Spring | – | – |
| `discovery-server` | **8761** | Eureka server. Service registry | – | – |
| `api-gateway` | **8080** | Single HTTP entry point. Routes `/{service-id}/**` to registered services | – | – |
| `command` | random (`server.port=0`) | Accepts store creation and rename requests. Owns the `StoreAggregate` | H2 `jdbc:h2:mem:command` (`STORE`, `GADGET`) | Command side: dispatches commands, publishes events |
| `query` | random (`server.port=0`) | Serves read requests from its own projections | H2 `jdbc:h2:mem:query` (`STORE`, `GADGET`, `STORE_ID_TO_GADGET`, `GADGET_TO_COUNTRY`) | Query side: event handlers update read models, query handlers answer reads |

Ports come from each module's `application.properties`.

---

## Project structure

```
cqrs-axon-demo/
├── pom.xml                          # parent POM: Spring Boot parent, Lombok, module list
├── stores.json                      # sample store payloads for POST /api/store
├── dto-event/                       # shared events and event payload classes (library)
│   └── src/main/java/com/cqrs/dto/event/store/
│       ├── StoreCreatedEvent.java
│       ├── StoreNameUpdatedEvent.java
│       └── GadgetState.java
├── discovery-server/
│   └── src/main/java/com/cqrs/discovery/server/DiscoveryServerApplication.java
├── api-gateway/
│   └── src/main/java/com/cqrs/apigateway/ApiGatewayApplication.java
├── command/
│   └── src/main/java/com/cqrs/command/
│       ├── CommandApplication.java
│       ├── aggregator/StoreAggregate.java        # event-sourced aggregate: validation, events
│       ├── config/SerializerConfiguration.java   # XStream serializer with a type allowlist
│       ├── controller/StoreController.java       # POST and PATCH /api/store
│       ├── handler/StoreEventHandler.java        # writes events to the command H2 tables
│       ├── model/command/                        # CreateStoreCommand, UpdateStoreNameCommand, GadgetCommand
│       ├── model/entity/                         # StoreEntity, GadgetEntity (JPA)
│       ├── model/rest/                           # StoreRequest, GadgetRequest, UpdateStoreNameRequest
│       ├── repository/                           # Spring Data repositories
│       └── service/ (StoreProcessingService, impl/StoreProcessingServiceImpl)
└── query/
    └── src/main/java/com/cqrs/query/
        ├── QueryApplication.java
        ├── config/SerializerConfiguration.java
        ├── controller/ (StoreController, GadgetController)  # GET endpoints
        ├── handler/
        │   ├── StoreEventHandler.java            # projects events into read models
        │   ├── StoreQueryHandler.java            # @QueryHandler for GetStoreByIdQuery
        │   └── GadgetQueryHandler.java           # @QueryHandler for gadget queries
        ├── model/entity/                         # read-model entities
        ├── model/query/                          # query messages
        ├── model/rest/                           # response DTOs
        ├── repository/                           # Spring Data repositories
        └── service/ (StoreService, GadgetService, impl/)
```

Each write-side and read-side module has the same layering: `controller` or `handler` (entry point) → `service` → `repository` → `model`.

---

## Tech stack

- Java 27 (`java.version`), compiled with `maven.compiler.release` **25**
- Spring Boot 3.5.3 (parent POM)
- Spring Cloud 2025.0.0: Eureka server and client, Spring Cloud Gateway
- Axon Framework 4.11.2 (`axon-spring-boot-starter`), with Axon Server as the event store and message router
- XStream as the Axon serializer for events, commands and messages
- Spring Web, Spring Data JPA, H2 in-memory databases
- Lombok 1.18.48
- Maven (multi-module)

---

## REST API

Requests can go directly to the services, or through the gateway on port 8080. The gateway routes by the lowercase Eureka service ID: `/command/...` and `/query/...`. The `/command` or `/query` prefix is removed before the request reaches the service.

Direct ports are random, so the examples use the gateway.

### Command side (`command`)

#### `POST /command/api/store`

Creates a store with its gadgets.

**Request body** (`StoreRequest`)

| Field | Type | Required | Description |
|---|---|---|---|
| `name` | string | yes | Store name. Must not be blank |
| `year` | int | yes | Founding year. Must be greater than 0 |
| `country` | string | no | Country of the store |
| `gadgets` | array | yes | At least one gadget. Each has `name`, `type`, `color` |

**Response:** `200 OK` with the new store ID as a plain string. The command is sent with `sendAndWait`, so the response comes after the aggregate has accepted the command.

```bash
curl -X POST http://localhost:8080/command/api/store \
  -H 'Content-Type: application/json' \
  -d '{
        "name": "TechWorld",
        "year": 2020,
        "country": "USA",
        "gadgets": [
          {"name": "SmartWatch Pro", "type": "Wearable", "color": "Black"},
          {"name": "Wireless Earbuds", "type": "Audio", "color": "White"}
        ]
      }'
```

A `stores.json` file with five sample stores is in the repository root.

#### `PATCH /command/api/store`

Renames an existing store.

**Request body** (`UpdateStoreNameRequest`)

| Field | Type | Required | Description |
|---|---|---|---|
| `storeId` | UUID string | yes | ID returned by `POST` |
| `name` | string | yes | New name. Must not be blank |

**Response:** `200 OK`, empty body.

```bash
curl -X PATCH http://localhost:8080/command/api/store \
  -H 'Content-Type: application/json' \
  -d '{"storeId": "<store-id>", "name": "TechWorld Plus"}'
```

### Query side (`query`)

Read endpoints. Each one sends a query through Axon and waits for the result with `join()`.

| Method | Path | Returns |
|---|---|---|
| `GET` | `/query/api/store/{storeId}` | `StoreResponse`: `id`, `name`, `year`, `country`, `gadgets[]` |
| `GET` | `/query/api/gadget/id/{gadgetId}` | `GadgetResponse`: `id`, `name`, `type`, `color` |
| `GET` | `/query/api/gadget/country/{country}` | Array of `GadgetToCountryResponse` (gadgets sold in a country) |
| `GET` | `/query/api/gadget/storeId/{storeId}` | Array of `StoreIdToGadgetResponse` (gadgets in a store) |

If a store or gadget ID is not found, the service returns `200 OK` with an empty object (all fields `null`). It does not return `404`.

```bash
curl http://localhost:8080/query/api/store/<store-id>
curl http://localhost:8080/query/api/gadget/country/USA
```

---

## Domain events and commands

### Commands (`command` module)

| Class | Fields | Handled by |
|---|---|---|
| `CreateStoreCommand` | `id` (target aggregate), `name`, `year`, `country`, `gadgets` | `StoreAggregate` constructor |
| `UpdateStoreNameCommand` | `id` (target aggregate), `name` | `StoreAggregate.handle` |

### Events (`dto-event` module)

| Class | Fields | Applied in `StoreAggregate` | Projected by |
|---|---|---|---|
| `StoreCreatedEvent` | `id`, `name`, `year`, `country`, `gadgets` (`GadgetState` list) | `on(StoreCreatedEvent)` sets all fields | command `StoreEventHandler` and query `StoreEventHandler` |
| `StoreNameUpdatedEvent` | `id`, `name` | `on(StoreNameUpdatedEvent)` sets `name` | command and query `StoreEventHandler` |

### Aggregate validation (`StoreAggregate`)

| Command | Rule | Error |
|---|---|---|
| `CreateStoreCommand` | `name` not blank | `Store name cannot be empty` |
| `CreateStoreCommand` | `year > 0` | `Invalid year` |
| `CreateStoreCommand` | at least one gadget | `Store must have at least one gadget` |
| `UpdateStoreNameCommand` | `id` not blank | `Store id cannot be empty` |
| `UpdateStoreNameCommand` | `name` not blank | `Store name cannot be empty` |

---

## Data model

### Command side (`command`, H2 `jdbc:h2:mem:command`)

| Table | Columns |
|---|---|
| `STORE` | `id` (PK), `name`, `"year"`, `country` |
| `GADGET` | `id` (PK), `name`, `type`, `color`, `store_id` |

Command tables are written only by the event handler, after events are published. They are used for reference, not for reads. Hibernate runs with `ddl-auto=update`.

### Query side (`query`, H2 `jdbc:h2:mem:query`)

| Table | Purpose | Columns |
|---|---|---|
| `STORE` | Store read model | `id` (PK), `name`, `"year"`, `country` |
| `GADGET` | Gadget read model | `id` (PK), `name`, `type`, `color`, `store_id` |
| `STORE_ID_TO_GADGET` | Gadgets grouped by store (`GET /api/gadget/storeId/...`) | `id` (random UUID), `store_id`, `gadget_id`, `name`, `type`, `color` |
| `GADGET_TO_COUNTRY` | Gadgets grouped by country (`GET /api/gadget/country/...`) | `id` (random UUID), `country`, `gadget_id`, `name`, `type`, `color` |

---

## Configuration reference

Each module has `src/main/resources/application.properties`.

| Property | Modules | Example | Purpose |
|---|---|---|---|
| `spring.application.name` | all | `command` | Service name. Also the Eureka service ID and gateway route prefix |
| `server.port` | gateway / discovery | `8080` / `8761` | Fixed HTTP port |
| `server.port` | command / query | `0` | Random port. Use Eureka or the gateway to reach them |
| `eureka.client.service-url.defaultZone` | command, query, gateway | `http://localhost:8761/eureka` | Eureka location |
| `eureka.client.register-with-eureka` / `fetch-registry` | discovery | `false` | The Eureka server does not register itself |
| `eureka.instance.prefer-ip-address` | command, query, discovery | `true` | Register the IP address rather than the hostname |
| `eureka.instance.instance-id` | command, query | `${spring.application.name}:${random.value}` | Unique ID per instance, so several can run side by side |
| `spring.cloud.gateway.server.webflux.discovery.locator.enabled` | gateway | `true` | Create a route per registered service |
| `spring.cloud.gateway.server.webflux.discovery.locator.lower-case-service-id` | gateway | `true` | Routes use `/command/...` rather than `/COMMAND/...` |
| `spring.datasource.url` | command, query | `jdbc:h2:mem:command;DB_CLOSE_DELAY=-1` | In-memory H2 database |
| `spring.datasource.username` / `password` | command, query | `root` / `root` | H2 credentials |
| `spring.jpa.hibernate.ddl-auto` | command, query | `update` | Hibernate creates or updates the tables |
| `spring.h2.console.enabled` | command, query | `true` | H2 web console |
| `axon.axonserver.servers` | query | `localhost:8124` | Axon Server address. Command uses the default (`localhost:8124`) |
| `axon.eventhandling.processors.store-group.mode` | command | `subscribing` | Command event handler runs in the publishing thread |
| `axon.eventhandling.processors.query-store-group.mode` | query | `tracking` | Query projection runs as a tracking processor |
| `axon.eventhandling.processors.query-store-group.thread-count` / `initial-segment-count` | query | `1` | One processing thread and one segment |
| `axon.serializer.general` / `events` / `messages` | command, query | `xstream` | Serializer for Axon payloads |

---

## Running locally

### Prerequisites

- **JDK 27** (or 25 or later, see [Build notes](#build-notes))
- **Maven 3.6.3+**
- **Axon Server** on `localhost:8124`. The repository does not include Axon Server, Docker or any setup for it. `command` and `query` will not start without it.

### Build

```bash
mvn clean install
```

### Start the services

Start them in this order, each in its own terminal:

```bash
# 1. Axon Server must already be running on localhost:8124
mvn -pl discovery-server spring-boot:run   # Eureka on :8761
mvn -pl command spring-boot:run            # random port, registers with Eureka
mvn -pl query spring-boot:run              # random port, registers with Eureka
mvn -pl api-gateway spring-boot:run        # gateway on :8080
```

Check registration at http://localhost:8761. `COMMAND`, `QUERY` and `API-GATEWAY` should appear.

---

## Trying it out

1. Create a store with the `POST` command above. Copy the returned ID.
2. Look it up through the query side:

   ```bash
   curl http://localhost:8080/query/api/store/<store-id>
   ```

3. Rename the store with `PATCH`, then call the `GET` again. The `name` should have changed. Query-side updates come through the tracking processor and may lag slightly behind the command.
4. Look up the gadgets by country and by store:

   ```bash
   curl http://localhost:8080/query/api/gadget/country/USA
   curl http://localhost:8080/query/api/gadget/storeId/<store-id>
   ```

5. The H2 consoles are optional. Each service serves one at `/h2-console` on its random port, which you can find in the Eureka dashboard. Use JDBC URL `jdbc:h2:mem:command` or `jdbc:h2:mem:query`, user `root`, password `root`.

---

## Build notes

- `java.version` is 27. The compiler targets `maven.compiler.release` **25**, which is set once in the parent POM and inherited by every module. Spring Boot 3.5.3's repackage plugin cannot read Java 27 class files (major version 71), so the build must target 25.
- Lombok is pinned to **1.18.48** in the parent `dependencyManagement`. Earlier versions fail on newer JDKs with a `TypeTag :: UNKNOWN` error during compilation.
- `dto-event` is a plain library. Its `spring-boot-maven-plugin` repackage execution is disabled (`phase: none`), so it produces a normal JAR.
- Code style follows **google-java-format**.
- There are no unit or integration tests in the repository.

---

## Known limitations

This is a demo. The following are known gaps.

**Error handling**

- Validation errors in `StoreAggregate` are thrown as `IllegalArgumentException`. No `@ExceptionHandler` maps them to a client error, so the response is Spring's default error response.
- A `POST` with `gadgets: null` fails with a `NullPointerException` in `StoreProcessingServiceImpl.createStore` before the aggregate validation runs.
- Read endpoints return `200 OK` with empty fields for unknown IDs instead of `404 Not Found`.

**Consistency**

- The query-side H2 database is in memory, but the tracking processor token is stored in Axon Server. After a restart of `query`, the read model can be empty while Axon Server believes the events were already processed. Restart both services together, or reset Axon Server, to avoid this.
- `StoreIdToGadgetEntity` and `GadgetToCountryEntity` use random UUIDs for their primary keys. If the same event is processed twice, rows are duplicated rather than replaced.
- The command side writes its tables from events in the same process that serves `POST`. There is no separate outbox and no retry logic.

**Domain and modelling**

- `StoreAggregate` has no check for duplicate store names, duplicate gadget IDs, or gadget changes after creation. Gadgets can only be set at creation.
- `UpdateStoreNameCommand` validates the name and ID but does not check that the store exists. An unknown ID is sent to the aggregate, which Axon rejects as an aggregate not found.
- `country` is optional on `POST` and is not validated.

**Security and operations**

- No authentication or authorization on any endpoint, including the H2 consoles.
- The H2 credentials are `root` / `root`.
- The `XStream` serializer allows `com.cqrs.dto.**`, `com.cqrs.query.**`, `java.util.*`, `java.lang.*` and array types. The `AnyTypePermission.ANY` line runs first, so the wildcard allowlist does not restrict deserialization in practice.
- No tests, no CI and no containerisation are included.
- `stores.json` is sample data for manual testing. No code loads it.
