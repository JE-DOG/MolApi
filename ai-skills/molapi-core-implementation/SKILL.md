---
name: molapi-core-implementation
description: Implement, modify, or review the MolApi core Kotlin Multiplatform library module. Use when a task touches molapi-core, dag.khinkal.molapi.core, generic ApiRequest/ApiResponse/ApiMock contracts, ApiRequestMatcher, ApiMockIdGenerator, ReadApiMockRegistry, ApiMockRegistry, InMemoryApiMockRegistry, core Gradle wiring, or core tests.
---

# MolApi Core Implementation

## Scope

Work only on `molapi-core` unless the user explicitly asks for cross-module changes. This module
owns the generic contracts that other MolApi libraries build on.

## Required Context

Read before editing:

- `AGENTS.md`
- `.agents/context/role-and-quality.md`
- `.agents/context/project-structure.md`
- `.agents/context/kmp-implementation-rules.md`
- `.agents/context/molapi-contract.md`
- `.agents/context/build-and-verification.md`

Inspect current local files before coding:

- `molapi-core/build.gradle.kts`
- `molapi-core/src/commonMain/kotlin/dag/khinkal/molapi/core/**`
- `molapi-core/src/commonTest/kotlin/dag/khinkal/molapi/core/**`

## Contract

Preserve these core concepts:

- `ApiRequest` and `ApiResponse` are marker-like public contracts.
- `ApiRequestMatcher<AR : ApiRequest>` uses `matches(request)`, not `match`.
- `ApiMock<Request, Matcher, Response, Id : Any>` carries `id`, `matcher`, and `response`.
- `ApiMockIdGenerator.generateId(matcher, response)` must not depend on a preexisting mock id.
- `ReadApiMockRegistry.find(request)` returns a nullable matching mock.
- `ApiMockRegistry.add(response, matcher)` generates the id.
- `ApiMockRegistry.add(mock)` must preserve the current semantic where the passed mock id is not
  reused.
- `remove(id)` removes all mocks with that id and returns whether anything was removed.
- `clear()` returns `Unit`.
- `find` returns the first mock whose matcher returns `true`.

## Implementation Rules

- Keep implementation in `commonMain` unless a platform API is unavoidable.
- Keep `explicitApi()` compatibility: public declarations need explicit visibility and return types.
- Do not add dependencies unless core cannot reasonably work without them.
- Do not introduce HTTP, Ktor, Room, Retrofit, Gson, Android assets, or UI concepts here.
- Prefer simple immutable data and focused generic types over extra layers.

## Tests

Add common tests for behavior changes:

- matcher delegation through `find`
- first-match ordering
- generated id behavior
- `add(mock)` id semantics
- `remove` return value and duplicate-id removal
- `clear` state reset
- registry `mocks` state updates

Use `kotlin.test` assertions and `*Test` class names.

## Validation

Run:

```bash
rtk ./gradlew :molapi-core:testAndroidHostTest :molapi-core:iosSimulatorArm64Test
```

For broader Gradle/source-set changes, also use `$molapi-change-validation`.
