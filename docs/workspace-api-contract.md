# Workspace API contract (Task 1)

Source of truth: checked-in Java controllers, DTOs, services, persistence mappings, and `V1__create_core_schema.sql`/`V2__schema_corrections.sql`.

## Authentication and errors

All `/api/workspaces/**` routes require `Authorization: Bearer <JWT>`. Unauthenticated requests return Spring Security's `401 Unauthorized` response. Application errors use RFC 7807 `ProblemDetail` JSON (`type`, `title`, `status`, optional `detail`, and validation `errors`). Validation failures are `400 Bad Request` with `title: "Validation failed"` and `errors: ["field: message"]`; stale/concurrent updates are `409 Conflict`; missing owned resources are `404 Not Found`.

CORS is not configured: `WebMvcConfig` only registers the current-user argument resolver, and `SecurityConfig` has no `CorsConfigurationSource`/`.cors(...)`. Therefore the deployed frontend origin is not confirmed as browser-compatible for authenticated requests or preflight.

## Workspace routes

### `POST /api/workspaces` — 201 Created

Request fields: `name` (required, non-blank, max 200), `description` (nullable), `roomType` (nullable), `designStyle` (nullable), `room` (required): positive `width`, `depth`, `height`, `wallThickness`, `appearance` (required): non-blank `wallColor`, `floorColor`, `ceilingColor`, nullable `lightPreset`, and nullable `budget`/`currency`. The request has no explicit budget or currency validation annotations.

Example:

```json
{"name":"Living Room","description":null,"roomType":"living","designStyle":"modern","room":{"width":4,"depth":5,"height":2.7,"wallThickness":0.15},"appearance":{"wallColor":"#FFFFFF","floorColor":"#8B5A2B","ceilingColor":"#FFFFFF","lightPreset":"day"},"budget":null,"currency":null}
```

Response is `WorkspaceDetailResponse` (same shape below), with `version: 0`, `furniture: []`, and `architecture: []`.

### `GET /api/workspaces` — 200 OK

Returns only the authenticated user's workspaces:

```json
[{"id":"uuid","name":"Living Room","roomType":"living","designStyle":"modern"}]
```

### `GET /api/workspaces/{workspaceId}` — 200 OK

Returns `WorkspaceDetailResponse` for an owned workspace. Other users receive `404`; missing authentication receives `401`.

### `PATCH /api/workspaces/{workspaceId}` — 200 OK

Request fields: nullable `name`, nullable `appearance` (`wallColor`, `floorColor`, `ceilingColor`, `lightPreset`), nullable `budget`, nullable `currency`, and required integer `expectedVersion`. Example: `{"name":"Renamed","appearance":{"wallColor":"#EEE","floorColor":"#654","ceilingColor":"#FFF","lightPreset":"night"},"expectedVersion":0}`. `room`/dimensions, `description`, `roomType`, and `designStyle` are not accepted by this DTO. A successful response is the updated detail response with incremented `version`; stale versions return `409`.

### `WorkspaceDetailResponse`

`{ id: UUID, name: string, description: string|null, room: { width: number, depth: number, height: number, wallThickness: number }, appearance: { wallColor: string, floorColor: string, ceilingColor: string, lightPreset: string }, budget: number|null, currency: string|null, version: integer, furniture: WorkspaceFurnitureResponse[], architecture: [] }`.

Creation persists room geometry and appearance. Update persists appearance, but cannot update room geometry because `UpdateWorkspaceRequest` has no room field. Detail responses include furniture, but architecture is always an empty array and is not loaded from persistence.

## Furniture routes

Base path: `/api/workspaces/{workspaceId}/furniture`.

- `GET` list: **not implemented** (no mapping in `WorkspaceFurnitureController`).
- `POST` add: **201 Created**. Request `{catalogItemId: UUID, position: {x:number,y:number,z:number}, rotation?: {x,y,z}, scale?: {x,y,z}}`; `catalogItemId` and `position` are required, vectors are `XyzDto` (no DTO validation constraints), and omitted rotation/scale default to `(0,0,0)`/`(1,1,1)`. Response is `WorkspaceFurnitureResponse`.
- `PATCH /{furnitureId}`: **200 OK**. Request fields `position?`, `rotation?`, `scale?`, `locked?`, `visible?`, `materialOverrides?`; response is `WorkspaceFurnitureResponse`.
- `DELETE /{furnitureId}`: **204 No Content**.

`WorkspaceFurnitureResponse` fields are `id`, `catalogItemId`, `position`, `rotation`, `scale`, `isLocked`, `isVisible`, and `materialOverrides`. Child-resource ownership is enforced by the application services; non-owned/missing resources resolve to `404`.

## Architecture routes

No `AddArchitectureRequest`, `UpdateArchitectureRequest`, or `WorkspaceArchitectureResponse` DTO exists. No architecture controller, application ports, or architecture HTTP routes exist. The table/entity exists, but architecture CRUD is a confirmed contract gap.

## Catalog and deployment gaps

There is no `V3__seed_catalog_items.sql` in the checked-in repository. The catalog module only exposes `existsById` internally through `CatalogItemQueryService`; there is no public slug-to-UUID/model-path endpoint. Frontend catalog mapping therefore has no supported HTTP contract.

The repository's current integration tests use Testcontainers PostgreSQL. They require a working Docker environment and do not exercise the deployed Render service. No production request was claimed or performed.

## Exact verification commands

- `mvn test` — not completed in this sandbox: Testcontainers could not find a valid Docker environment.
- `mvn package` — run separately after test changes; result is recorded in the task summary.
