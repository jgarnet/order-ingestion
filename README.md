# order-ingestion API

## Overview

Basic batch API which ingests customer orders and inserts into a database.

This project serves as a sandbox to explore different tools and approaches for processing batch payloads.

## Architecture

### Domain Model Layer

The project uses [POJOs](./src/main/java/org/myshop/order) to represent the domain model and encapsulate validation logic.

### Dependency Injection Layer

The project is using Dagger 2 for dependency injection, leveraging a global [App Module](./src/main/java/org/myshop/di/AppModule.java) and [App Component](./src/main/java/org/myshop/di/AppComponent.java).

### HTTP Layer

The HTTP layer uses a simple Java [HTTP Server implementation](./src/main/java/org/myshop/http/OrdersHttpServer.java) as a front controller for handling HTTP requests. Request handlers are split across multiple threads using the ExecutorService.

Operations include [batch order ingestion](./src/main/java/org/myshop/http/BatchOrdersHandler.java) and [error store operations](./src/main/java/org/myshop/http/ErrorStoreHandler.java).

### Batch Processing Layer

The batch layer provides a [batch service](./src/main/java/org/myshop/batch/BatchOrdersService.java) which delegates processing to several [processors](./src/main/java/org/myshop/batch/BatchOrdersProcessor.java) split across multiple threads using the ExecutorService.

### Queue Layer

The queue layer provides [basic queue operations](./src/main/java/org/myshop/queue/QueueProvider.java) and an [error store](./src/main/java/org/myshop/queue/ErrorStore.java) for batches which have exceeded the retry threshold.

### Persistence Layer

The persistence layer provides [database](./src/main/java/org/myshop/persistence/database/Database.java) access, leveraging [HikariCP](./src/main/java/org/myshop/persistence/database/HikariDatabase.java) for connection pooling.

The [repository](./src/main/java/org/myshop/persistence/repository) layer provides a simple native SQL implementation for data source interactions.

## Running Locally

The project leverages [Docker](./Dockerfile) and [docker-compose](./local/docker-compose.yml) for local use.

The [scripts](./scripts) directory provides scripts for quickly standing up and tearing down the Docker resources:
- [docker-run.sh](./scripts/docker-run.sh) builds and runs the containers in docker-compose.yml
- [docker-down.sh](./scripts/docker-down.sh) tears down the containers

## Submitting Payloads

The [client](./client) subproject provides a simple NodeJS implementation that creates sample order batches and submits them to the API.