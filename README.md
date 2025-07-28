# Bookly - Developer Guide

## Overview
Bookly is a Spring Boot application for managing bookstore inventories and catalogs, following Domain-Driven Design (DDD) and Clean Architecture principles. The project is written in Kotlin and uses Gradle for builds.

## Code Structure

```
com.bookly
├── book
│   ├── domain
│   │   ├── model
│   │   ├── valueobject
│   │   └── repository
│   ├── application
│   └── infrastructure
│       ├── repository
│       └── rest
├── bookstore
│   ├── domain
│   │   ├── model
│   │   ├── valueobject
│   │   └── repository
│   ├── application
│   └── infrastructure
│       ├── repository
│       └── rest
├── shared
│   ├── domain
│   └── infrastructure
└── BooklyApplication.kt
```

- **Domain Layer**: Contains core business entities, value objects, and aggregates. Primitives are avoided in favor of value objects (e.g., `BookId`, `BookstoreId`, `BookTitle`, `Location`).
- **Application Layer**: Contains service classes orchestrating domain logic and use cases (e.g., `BookstoreService`).
- **Infrastructure Layer**: Contains REST controllers, DTOs, and persistence adapters. Controllers are split by API audience:
  - `/api/bookstores` for internal bookstore operations (inventory management)
  - `/api/public` for public client operations (search, discovery)
- **Test Layer**: Acceptance tests are in `src/test/kotlin/com/bookly/acceptance`. Test utilities are split:
  - `StoreTestUtil` for bookstore operations
  - `ClientTestUtil` for client search operations

## Conventions & Patterns

- **Domain-Driven Design (DDD)**: Aggregates, entities, and value objects are used to model business logic. Primitives are wrapped in value objects to prevent mistakes and enforce invariants.
- **Clean Architecture**: Business logic is separated from infrastructure concerns. Application services orchestrate domain objects, and controllers handle HTTP requests.
- **Repository Pattern**: Domain repositories abstract persistence logic (currently in-memory, will be replaced with JPA).
- **Service Layer**: Application services expose business use cases and coordinate domain objects.
- **REST API Segregation**: Internal and public APIs are separated for clarity and security.
- **Testing**: Acceptance tests interact with the system as a black box, using only public APIs. Test utilities are injected as Spring components for clean setup.

## How to Contribute

- Follow the existing package structure and naming conventions.
- Use value objects for all domain identifiers and attributes (avoid primitives).
- Add new features in the appropriate domain, application, or infrastructure layer.
- Write acceptance tests for new features, using `StoreTestUtil` and `ClientTestUtil`.
- Run tests with `./gradlew test`.

## Patterns in Use
- **Aggregate Root**: `Bookstore` manages its books and inventory.
- **Value Objects**: `BookId`, `BookstoreId`, `BookstoreName`, `Location`, etc.
- **Service Layer**: `BookstoreService` orchestrates domain logic.
- **Repository Pattern**: Abstracts persistence for domain objects.
- **REST API Segregation**: Internal vs. public endpoints.

## Example Workflow
1. Bookstore publishes inventory via `/api/bookstores/bookstores`.
2. Clients search for books near their location via `/api/public/inventory/search?isbn=...&location=...`.
3. Acceptance tests verify business scenarios using injected test utilities.

## Contact
For questions or contributions, please contact the maintainers or open an issue in the repository.
