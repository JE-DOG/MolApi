---
name: molapi-http-implementation
description: Implement, modify, or review the MolApi HTTP Kotlin Multiplatform library module. Use when a task touches molapi-http, dag.khinkal.molapi.http, HttpRequest, HttpResponse, HttpUrl, Headers, HttpBody, JsonBody, JsonHttpBody, HttpMethod, BaseHttpRequestMatcher, RawHttpUrlRequestMatcher, HttpApiMockRegistry, HTTP DSL helpers, URL/query/header matching, HTTP Gradle wiring, or HTTP tests.
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

Treat current module sources and tests as authoritative when a context document lags behind the
implemented API.

## Contract

Preserve the HTTP model:

- `HttpMethod` supports `GET`, `POST`, `PUT`, `PATCH`, `HEAD`, and `DELETE`.
- `Headers` wraps `Map<String, List<String>>`; keep helper semantics such as JSON content headers
  aligned with existing code.
- `HttpBody` is the body abstraction; `JsonHttpBody` exposes raw JSON text and `JsonBody` is its
  standard implementation.
- `HttpUrl` stores optional `scheme`, `host`, `port`, and `path` plus ordered, duplicate-preserving
  `Map<String, List<String>>` query values.
- `HttpRequest` implements core `ApiRequest`, uses `HttpUrl`, and allows nullable headers and body.
- `HttpResponse` implements core `ApiResponse`, allows nullable headers and body, and requires a
  status code in `100..599`.
- `HttpApiMockRegistry` is the HTTP registry type alias over core registry types.
- `HttpInMemoryApiMockRegistry` is the in-memory HTTP registry.
- `BaseHttpRequestMatcher` matches optional `url`, `method`, `headers`, and `body`.
- `RawHttpUrlRequestMatcher` uses `HttpUrl.toRawUrl().contains(rawUrl)` and can additionally match
  method, headers, and body.

Matcher behavior:

- `null` matcher fields mean "ignore this dimension".
- An empty structured `HttpUrl` matcher accepts any request URL.
- Scheme and host compare case-insensitively; port uses equality; configured path uses substring
  matching.
- Every configured query name must have the same ordered value list; extra request query names are
  allowed.
- Method, headers, and body use exact equality when configured.
