---
name: molapi-http-serialization-implementation
description: Implement, modify, or review the MolApi kotlinx.serialization HTTP helper module. Use when a task touches molapi-http-serialization, dag.khinkal.molapi.http.serializable, SerializableJsonBody, JsonHttpResponse, kotlinx.serialization Gradle wiring, or common tests for serialization HTTP helpers.
---

# MolApi Serialization Implementation

## Scope

Work on `molapi-http-serialization`, the optional common `kotlinx.serialization` helper module for
`molapi-http`.

## Required Context

Read before editing:

- `AGENTS.md`
- `.agents/context/role-and-quality.md`
- `.agents/context/project-structure.md`
- `.agents/context/kmp-implementation-rules.md`
- `.agents/context/build-and-verification.md`

Inspect current local files:

- `molapi-http-serialization/build.gradle.kts`
- `molapi-http-serialization/src/commonMain/kotlin/dag/khinkal/molapi/http/serializable/**`
- `molapi-http-serialization/src/commonTest/kotlin/dag/khinkal/molapi/http/**`

Use `ksrc --help` before inspecting kotlinx.serialization dependency sources.

## Contract

Preserve helper shape:

- `SerializableJsonBody(body, json)` encodes a serializable value into `JsonBody`.
- `JsonHttpResponse(serializableBody, headers, statusCode, json)` builds `HttpResponse`.
- Default headers should stay aligned with existing JSON content behavior.
- Public APIs expose `molapi-http` and `kotlinx.serialization` types intentionally.

## Implementation Rules

- Keep code in `commonMain`.
- Keep this module optional; do not move serialization into `molapi-http`.
- Apply `libs.plugins.kotlinSerialization`.
- Use `api(project(":molapi-http"))` and `api(libs.kotlinx.serialization.json)`.
- Do not add Gson, Ktor, Retrofit, Room, Android assets, or UI concepts here.

## Tests

Cover:

- serializable object/list encoding
- custom `Json` instance behavior
- response status/header/body construction
- default JSON header behavior

## Validation

Run:

```bash
rtk ./gradlew :molapi-http-serialization:testAndroidHostTest :molapi-http-serialization:iosSimulatorArm64Test
```

If sample uses changed APIs, run sample checks through `$molapi-change-validation`.
