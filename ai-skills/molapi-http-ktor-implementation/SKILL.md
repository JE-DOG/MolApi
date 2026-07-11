---
name: molapi-http-ktor-implementation
description: Implement, modify, or review the MolApi Ktor Client integration module. Use when a task touches molapi-http-ktor, dag.khinkal.molapi.http.ktor, MolApiKtorPlugin, Ktor request/response adapters, Ktor Client Send hook behavior, Ktor Gradle wiring, or Ktor integration tests.
---

# MolApi Ktor Implementation

## Scope

Work on `molapi-http-ktor`, the common Ktor Client adapter for `molapi-http`. This module must stay
client-side and multiplatform.

## Required Context

Read before editing:

- `AGENTS.md`
- `.agents/context/role-and-quality.md`
- `.agents/context/project-structure.md`
- `.agents/context/kmp-implementation-rules.md`
- `.agents/context/molapi-contract.md`
- `.agents/context/build-and-verification.md`

Inspect current local files:

- `molapi-http-ktor/build.gradle.kts`
- `molapi-http-ktor/src/commonMain/kotlin/dag/khinkal/molapi/http/ktor/**`
- `molapi-http-ktor/src/commonTest/kotlin/dag/khinkal/molapi/http/ktor/**`

Use `ksrc --help` before inspecting Ktor dependency sources.

## Contract

Preserve behavior:

- `MolApiKtorPlugin` requires a registry configured as
  `ReadApiMockRegistry<HttpRequest, ApiRequestMatcher<HttpRequest>, HttpResponse, *>`.
- Convert supported Ktor requests to MolApi `HttpRequest`.
- Convert MolApi `HttpResponse` to a Ktor client call/response.
- Hook into Ktor Client send flow.
- If request conversion fails, method is unsupported, or no mock is found, proceed with the real
  Ktor request.
- Supported methods must match `HttpMethod`.

## Implementation Rules

- Do not add Ktor server APIs.
- Do not add delay, fallback settings, semantic JSON matching, URL model, or header matching unless
  explicitly requested.
- Keep dependencies scoped to Ktor Client.
- Keep adapter logic small and testable.
- Preserve response status, headers, body, request association, and receive pipeline behavior.

## Tests

Use common tests with Ktor Client test utilities. Cover:

- mocked response short-circuit
- no-mock pass-through
- unsupported method pass-through
- request URL/method/body/header conversion
- response status/header/body conversion
- plugin configuration error when registry is missing

Use `kotlinx.coroutines.test` where suspend client behavior is involved.

## Validation

Run:

```bash
rtk ./gradlew :molapi-http-ktor:testAndroidHostTest :molapi-http-ktor:iosSimulatorArm64Test
```

If sample Ktor usage changes, also run sample checks through `$molapi-change-validation`.
