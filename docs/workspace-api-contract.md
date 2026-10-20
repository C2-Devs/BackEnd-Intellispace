# Workspace API contract

Source of truth: the checked-in Java controllers, DTOs, services, persistence mappings, and Flyway migrations. This document describes the current repository state; it is not a contract for the deployed Render service.

## Runtime and cross-cutting behavior

- The project uses Spring Boot `3.5.7` and Java 21.
- `WebMvcConfig` registers the current-user argument resolver. There is no `CorsConfig` or `CorsConfigurationSource` in this checkout, so browser CORS compatibility for an external frontend is not confirmed.
- `/api/workspaces/**` requires `Authorization: Bearer <JWT>`. Missing authentication is rejected with `401 Unauthorized` by Spring Security.
- Application errors use Spring `ProblemDetail` JSON. Validation failures are `400 Bad Request` with title `Validation failed` and an `errors` array containing `field: message` strings. Missing/unauthorized-owned resources return `404 Not Found`; stale workspace versions and locked furniture return `409 Conflict`.

## Workspace routes

### `POST /api/workspaces` — `201 Created`

`CreateWorkspaceRequest` fields:

- `name`: required, non-blank, max 200 characters.
- `description`, `roomType`, `designStyle`, `budget`, and `currency`: nullable; no explicit validation annotations on these fields.
- `room`: required; `width`, `depth`, `height`, and `wallThickness` are positive numbers.
- `appearance`: required; `wallColor`, `floorColor`, and `ceilingColor` are non-blank; `lightPreset` is nullable.

The response is `WorkspaceDetailResponse`. A newly created workspace has `version: 0`, `furniture: []`, and `architecture: []`.

### `GET /api/workspaces` — `200 OK`

Returns only the authenticated user's summaries: `id`, `name`, `roomType`, and `designStyle`.

### `GET /api/workspaces/{workspaceId}` — `200 OK`

Returns the owned workspace detail. The detail response contains `id`, `name`, nullable `description`, room geometry, appearance, nullable `budget` and `currency`, integer `version`, embedded `furniture`, and `architecture: []`. Furniture is embedded here; there is no dedicated furniture-list HTTP mapping.

### `PATCH /api/workspaces/{workspaceId}` — `200 OK`

`UpdateWorkspaceRequest` accepts nullable `name`, nullable `appearance` (`wallColor`, `floorColor`, `ceilingColor`, `lightPreset`), nullable `budget`, nullable `currency`, and required integer `expectedVersion`. It does not accept `description`, `roomType`, `designStyle`, or room geometry. A successful update increments `version`; a stale version returns `409 Conflict`.

## Furniture routes

Base path: `/api/workspaces/{workspaceId}/furniture`.

- `GET`: no dedicated mapping. Furniture is available through the workspace-detail response.
- `POST`: `201 Created`; accepts required `catalogItemId` and `position`, with optional `rotation` and `scale`. Omitted values receive service defaults. The response contains `id`, `catalogItemId`, `position`, `rotation`, `scale`, `isLocked`, `isVisible`, and `materialOverrides`.
- `PATCH /{furnitureId}`: `200 OK`; updates placement and optional lock/visibility/material override fields.
- `DELETE /{furnitureId}`: `204 No Content`.

Furniture child-resource ownership is enforced by the application services; missing or non-owned resources resolve to `404 Not Found`.

## Architecture and catalog gaps

This checkout contains `WorkspaceArchitectureEntity`, its JPA repository, and architecture domain enums, but no `WorkspaceArchitectureController`, architecture request/response DTOs, application ports, or architecture HTTP mappings. Architecture CRUD is therefore a confirmed contract gap, and the `architecture` field in workspace details is always an empty list.

The migrations present are `V1__create_core_schema.sql` and `V2__schema_corrections.sql`; there is no `V3__seed_catalog_items.sql`. Catalog lookup is internal (`existsById`); there is no public catalog HTTP endpoint for slug-to-UUID or model-path lookup. Seed data, if added in another environment, must not be interpreted as an HTTP catalog contract.

## Verification scope

`WorkspaceApiIT` and the other `*IT` tests use Testcontainers PostgreSQL and exercise the application locally through Spring/MockMvc. They do not verify the deployed Render service. The Maven Surefire configuration explicitly includes `**/*IT.java`, so `mvn -B -ntp verify` executes `WorkspaceApiIT` along with the unit tests.

In this sandbox, `mvn` is unavailable (`mvn: command not found`) and no Maven Wrapper is checked in, so Maven verification could not be executed here. The intended CI command is:

```text
mvn -B -ntp verify
```

GitHub Actions provisions Temurin JDK 21, caches Maven dependencies, and runs this command on a Docker-capable GitHub-hosted runner for Testcontainers.
