---
name: molapi-http-retrofit-implementation
description: Implement, modify, or review the MolApi Retrofit and OkHttp Android integration module. Use when a task touches molapi-http-retrofit, dag.khinkal.molapi.http.retrofit, MolApiRetrofitInterceptor, OkHttp request/response adapters, addMolApiInterceptor, Retrofit Gradle wiring, or Android host tests for Retrofit integration.
---

# MolApi Retrofit Implementation

## Scope

Work on `molapi-http-retrofit`, the Android-only OkHttp/Retrofit adapter for `molapi-http`.

## Required Context

Read before editing:

- `AGENTS.md`
- `.agents/context/role-and-quality.md`
- `.agents/context/project-structure.md`
- `.agents/context/kmp-implementation-rules.md`
- `.agents/context/molapi-contract.md`
- `.agents/context/build-and-verification.md`

Inspect current local files:

- `molapi-http-retrofit/build.gradle.kts`
- `molapi-http-retrofit/src/androidMain/kotlin/dag/khinkal/molapi/http/retrofit/**`
- `molapi-http-retrofit/src/androidHostTest/kotlin/dag/khinkal/molapi/http/retrofit/**`
- current `ReadApiMockRegistry`, `HttpRequest`, `HttpUrl`, and `Headers` declarations in
  `molapi-core` and `molapi-http`

Use `ksrc --help` before inspecting OkHttp or Retrofit dependency sources.

Treat current module sources and tests as authoritative when a context document lags behind the
implemented API.

## Contract

Preserve behavior:

- `MolApiRetrofitInterceptor` reads the OkHttp request.
- `OkHttpClient.Builder.addMolApiInterceptor` add mocks registry.
- Its registry type is
  `ReadApiMockRegistry<HttpRequest, ApiRequestMatcher<HttpRequest>, HttpResponse>`.

## Usage example

```kotlin
val okHttpClient = OkHttpClient.Builder()
    .addMolApiInterceptor(registry)
    .build()
```
