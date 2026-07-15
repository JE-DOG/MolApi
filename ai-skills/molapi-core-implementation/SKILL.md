---
name: molapi-core-implementation
description: Implement, modify, or review the MolApi core Kotlin Multiplatform library module. Use when a task touches molapi-core, dag.khinkal.molapi.core, generic ApiRequest/ApiResponse/ApiMock contracts, String mock ids, ApiRequestMatcher, ApiMockIdGenerator, UuidApiMockIdGenerator, ReadApiMockRegistry, ApiMockRegistry, StateFlow-backed registry state, core Gradle wiring, or core tests.
---

# MolApi Core Implementation

## Scope

Work only on `molapi-core` unless the user explicitly asks for cross-module changes. This module
owns the generic contracts that other MolApi libraries build on.

## Contract

Preserve these core concepts:

- `ApiRequest` and `ApiResponse` are marker-like public contracts.
- `ApiRequestMatcher<AR : ApiRequest>` uses `matches(request)`, not `match`.
- `ApiMock<Request, Matcher, Response>` carries a `String` `id`, `matcher`, and `response`.
- `ApiMockIdGenerator<Request, Matcher, Response>.generateId(matcher, response)` returns a
  `String` and must not depend on a preexisting mock id.
- `UuidApiMockIdGenerator` is the default implementation used by higher-level registries and
  generates a new UUID string for each call.
- `ReadApiMockRegistry.find(request)` returns a nullable matching mock.
- `ApiMockRegistry.mocks` exposes the current ordered list as a read-only `StateFlow`.
- `ApiMockRegistry.add(response, matcher)` generates the id.
- `ApiMockRegistry.add(mock)` must preserve the current semantic where the passed mock id is not
  reused.
- `remove(id: String)` removes all mocks with that id and returns whether anything was removed.
- `clear()` returns `Unit`.
- `find` returns the first mock whose matcher returns `true`.
