---
name: molapi-http-ktor-implementation
description: Implement, modify, or review the MolApi Ktor Client integration module. Use when a task touches molapi-http-ktor, dag.khinkal.molapi.http.ktor, MolApiKtorPlugin, Ktor request/response adapters, structured HttpUrl/query/header conversion, Ktor Client Send and receive pipeline behavior, Ktor Gradle wiring, or Ktor integration tests.
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
  `ReadApiMockRegistry<HttpRequest, ApiRequestMatcher<HttpRequest>, HttpResponse>`.
- Convert supported Ktor requests to MolApi `HttpRequest`, including protocol, host, effective port,
  encoded path, ordered query values, headers, and body.
- Convert MolApi `HttpResponse` to a Ktor client call/response.
- Hook into Ktor Client send flow.
- If request conversion fails, method is unsupported, or no mock is found, proceed with the real
  Ktor request.
- Supported methods must match `HttpMethod`.

## Usage example

```kotlin
import dag.khinkal.molapi.http.dsl.get
import dag.khinkal.molapi.http.dsl.http
import dag.khinkal.molapi.http.dsl.patch
import dag.khinkal.molapi.http.dsl.post
import dag.khinkal.molapi.http.dsl.put
import dag.khinkal.molapi.http.model.Headers
import dag.khinkal.molapi.http.model.HttpMethod
import dag.khinkal.molapi.http.model.HttpResponse
import dag.khinkal.molapi.http.model.HttpUrl
import dag.khinkal.molapi.http.model.JsonBody
import dag.khinkal.molapi.http.registry.HttpInMemoryApiMockRegistry

val registry = HttpInMemoryApiMockRegistry().apply {
    // A JSON string shortcut.
    get(
        path = "/todos",
        json = """[{"id":1,"title":"from molapi","completed":false}]""",
    )

    // A full response, including custom headers and status.
    post(
        url = HttpUrl(path = "/todos"),
        headers = Headers.jsonContent(),
        body = JsonBody("""{"title":"new"}"""),
        response = HttpResponse(
            body = JsonBody("""{"id":2,"title":"new","completed":false}"""),
            headers = Headers.jsonContent(),
            statusCode = 201,
        ),
    )

    // An arbitrary response body with its metadata.
    put(
        path = "/todos/2",
        response = JsonBody("""{"completed":true}"""),
        responseHeaders = Headers.jsonContent(),
        statusCode = 202,
    )

    // A response produced lazily from the request registration code.
    patch("/todos/2") {
        HttpResponse(body = JsonBody("""{"completed":false}"""))
    }

    // Without selected http method
    http(
        method = HttpMethod.DELETE,
        path = "/todos/2",
        response = HttpResponse(statusCode = 204),
    )
}
```

- Preferred with lambda for comfort reading
- First parameter in dsl without already set http method is url path
