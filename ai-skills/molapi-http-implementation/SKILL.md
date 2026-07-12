---
name: molapi-http-implementation
description: Implement, modify, or review the MolApi HTTP Kotlin Multiplatform library module. Use when a task touches molapi-http, dag.khinkal.molapi.http, HttpRequest, HttpResponse, Headers, HttpBody, JsonBody, HttpMethod, BaseHttpRequestMatcher, HttpApiMockRegistry, HTTP DSL helpers, HTTP Gradle wiring, or HTTP tests.
---

# MolApi HTTP Implementation

## Scope

Work on `molapi-http`, the common HTTP specialization over `molapi-core`. Do not implement client
integrations here; Ktor, Retrofit, assets, Gson, serialization, editor, and Room have separate
skills.

## Required Context

Read before editing:

- `AGENTS.md`
- `.agents/context/role-and-quality.md`
- `.agents/context/project-structure.md`
- `.agents/context/kmp-implementation-rules.md`
- `.agents/context/molapi-contract.md`
- `.agents/context/build-and-verification.md`

Inspect current local files:

- `molapi-http/build.gradle.kts`
- `molapi-http/src/commonMain/kotlin/dag/khinkal/molapi/http/**`
- `molapi-http/src/commonTest/kotlin/dag/khinkal/molapi/http/**`

## Contract

Preserve the HTTP model:

- `HttpMethod` supports only `GET`, `POST`, `PUT`, `PATCH`, `DELETE`.
- `Headers` wraps `Map<String, List<String>>`; keep helper semantics such as JSON content headers
  aligned with existing code.
- `HttpBody` is the body abstraction; `JsonBody` stores raw JSON text.
- `HttpRequest` implements core `ApiRequest`.
- `HttpResponse` implements core `ApiResponse`.
- `HttpApiMockRegistry` is the HTTP registry type alias over core registry types.
- `HttpInMemoryApiMockRegistry` is the in-memory HTTP registry.
- `BaseHttpRequestMatcher` matches optional `url`, `method`, and `body`.

Matcher behavior:

- `null` matcher fields mean "ignore this dimension".
- If all matcher fields are `null`, the matcher accepts any request.
- Equality is structural and string-based; do not add URL parsing or semantic JSON matching without
  explicit request.

## Implementation Rules

- Keep HTTP APIs in `commonMain`.
- Use `api(project(":molapi-core"))` because public HTTP types expose core contracts.
- Keep DSL overloads predictable across methods.
- Do not add header matching, query parsing, API keys, delay, fallback behavior, or client-specific
  APIs unless requested.
- Do not import Ktor, OkHttp, Retrofit, Android, Room, Gson, or Compose here.

## Tests

Add common tests for:

- matcher positive and negative cases
- wildcard matcher behavior
- method/url/body DSL registration
- default status/header/body behavior
- registry integration through HTTP aliases

Use focused tests near existing `molapi-http/src/commonTest`.

## Validation

Run:

```bash
rtk ./gradlew :molapi-http:testAndroidHostTest :molapi-http:iosSimulatorArm64Test
```

If public HTTP API changes, also run affected integration module tests and
`$molapi-change-validation`.
