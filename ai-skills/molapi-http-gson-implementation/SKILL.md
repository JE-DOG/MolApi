---
name: molapi-http-gson-implementation
description: Implement, modify, or review the MolApi Gson Android helper module. Use when a task touches molapi-http-gson, dag.khinkal.molapi.http.gson, GsonJsonBody, GsonHttpResponse, JsonBody.fromGson, Gson Gradle wiring, or Android host tests for Gson HTTP response/body helpers.
---

# MolApi Gson Implementation

## Scope

Work on `molapi-http-gson`, the Android-only optional Gson helper module for `molapi-http`.

## Contract

Preserve helper shape:

- `GsonJsonBody(data, gson)` serializes data with Gson and returns `JsonBody`.
- `GsonHttpResponse(headers, body, statusCode, gson)` returns `HttpResponse`.
- `JsonBody.fromGson(data, gson)` delegates to `GsonJsonBody`.
- Public helper APIs expose `molapi-http` types.
