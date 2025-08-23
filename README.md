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

#### API
### Location & Discovery Context
# Search for books by location and criteria
GET /books?location={lat},{lng}&radius={km}&query={searchTerm}&genre={genre}
# Search for bookstores by location
GET /bookstores?location={lat},{lng}&radius={km}&name={storeName}
# Get detailed book information
GET /books/{isbn}
# Get bookstore details and their inventory
GET /bookstores/{bookstoreId}
GET /bookstores/{bookstoreId}/inventory?available=true&genre={genre}

### User Management Context
# User registration and profile management
POST /users
GET /users/{userId}
PUT /users/{userId}
# User's rental history and current reservations
GET /users/{userId}/reservations?status={active|completed|cancelled}
GET /users/{userId}/rental-history

### Bookstore management of content
# Bookstore registration and profile
POST /bookstores
GET /bookstores/{bookstoreId}
PUT /bookstores/{bookstoreId}
# Inventory management for bookstore owners
GET /bookstores/{bookstoreId}/inventory
POST /bookstores/{bookstoreId}/inventory/books
PUT /bookstores/{bookstoreId}/inventory/books/{isbn}
DELETE /bookstores/{bookstoreId}/inventory/books/{isbn}
# Rental settings (prices, durations)
GET /bookstores/{bookstoreId}/rental-settings
PUT /bookstores/{bookstoreId}/rental-settings

### Book Rental Context
# Reservation lifecycle
POST /reservations
GET /reservations/{reservationId}
PUT /reservations/{reservationId}/confirm    # Confirm pickup
PUT /reservations/{reservationId}/return     # Return book
DELETE /reservations/{reservationId}         # Cancel reservation
# Check book availability at specific bookstore
GET /bookstores/{bookstoreId}/books/{isbn}/availability

### Inventory Management Context
# Real-time availability checking
GET /inventory/books/{isbn}/availability?location={lat},{lng}&radius={km}
# Inventory updates (for bookstore owners)
PUT /inventory/books/{isbn}/status
GET /inventory/books/{isbn}/rental-history