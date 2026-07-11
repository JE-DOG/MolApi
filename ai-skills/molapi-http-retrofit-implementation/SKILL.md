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

Use `ksrc --help` before inspecting OkHttp or Retrofit dependency sources.

## Contract

Preserve behavior:

- `MolApiRetrofitInterceptor` reads the OkHttp request.
- Convert supported OkHttp requests to MolApi `HttpRequest`.
- Find a mock through `ReadApiMockRegistry`.
- Return a synthetic OkHttp response when a mock exists.
- Call `chain.proceed(request)` when conversion fails or no mock exists.
- `OkHttpClient.Builder.addMolApiInterceptor(registry)` adds the interceptor.

## Implementation Rules

- Keep all Retrofit/OkHttp code in Android source sets.
- Do not add iOS/common targets for this module.
- Keep public APIs Android-compatible and explicit.
- Keep unsupported HTTP methods as pass-through.
- Preserve request, status code, headers, media/body behavior in synthetic responses.

## Tests

Use Android host tests for:

- interceptor returns mock response
- interceptor proceeds when no mock exists
- unsupported methods proceed
- request body conversion
- response headers/status/body conversion
- builder extension adds the interceptor

Use MockWebServer or focused OkHttp interceptor tests following existing module style.

## Validation

Run:

```bash
rtk ./gradlew :molapi-http-retrofit:testAndroidHostTest
```

If shared HTTP contracts changed too, run `$molapi-http-implementation` validation and
`$molapi-change-validation`.
