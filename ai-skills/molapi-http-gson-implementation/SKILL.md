---
name: molapi-http-gson-implementation
description: Implement, modify, or review the MolApi Gson Android helper module. Use when a task touches molapi-http-gson, dag.khinkal.molapi.http.gson, GsonJsonBody, GsonHttpResponse, Gson Gradle wiring, or Android host tests for Gson HTTP response/body helpers.
---

# MolApi Gson Implementation

## Scope

Work on `molapi-http-gson`, the Android-only optional Gson helper module for `molapi-http`.

## Required Context

Read before editing:

- `AGENTS.md`
- `.agents/context/role-and-quality.md`
- `.agents/context/project-structure.md`
- `.agents/context/kmp-implementation-rules.md`
- `.agents/context/build-and-verification.md`

Inspect current local files:

- `molapi-http-gson/build.gradle.kts`
- `molapi-http-gson/src/androidMain/kotlin/dag/khinkal/molapi/http/gson/**`
- `molapi-http-gson/src/androidHostTest/kotlin/dag/khinkal/molapi/http/gson/**`

Use `ksrc --help` before inspecting Gson dependency sources.

## Contract

Preserve helper shape:

- `GsonJsonBody(data, gson)` serializes data with Gson and returns `JsonBody`.
- `GsonHttpResponse(headers, body, statusCode, gson)` returns `HttpResponse`.
- Public helper APIs expose `molapi-http` types.

## Implementation Rules

- Keep code in `androidMain`.
- Keep tests in `androidHostTest`.
- Do not add common/iOS targets unless the dependency strategy changes explicitly.
- Use `api(project(":molapi-http"))` and `api(libs.gson)` when public APIs expose these types.
- Do not move Gson helpers into `molapi-http`; serialization helpers must remain optional.

## Tests

Cover:

- object serialization to `JsonBody`
- custom Gson instance behavior when relevant
- response status/header/body construction
- nullability/defaults matching current public contract

## Validation

Run:

```bash
rtk ./gradlew :molapi-http-gson:testAndroidHostTest
```

If used from sample or Retrofit tests, also run `$molapi-change-validation`.
