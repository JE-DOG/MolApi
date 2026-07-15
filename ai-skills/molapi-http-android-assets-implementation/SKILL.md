---
name: molapi-http-android-assets-implementation
description: Implement, modify, or review the MolApi Android assets HTTP helper module. Use when a task touches molapi-http-android-assets, dag.khinkal.molapi.http.assets, AssetJsonBody, JsonBody.fromAssets, Android resource/test asset Gradle wiring, Robolectric tests, or Android host tests for asset-backed JSON mocks.
---

# MolApi Android Assets Implementation

## Scope

Work on `molapi-http-android-assets`, the Android-only helper module that loads `JsonBody` from
Android assets.

## Contract

Preserve helper shape:

- `AssetJsonBody(context, path, charset)` loads from `context.assets`.
- `AssetJsonBody(assetManager, path, charset)` loads from `AssetManager`.
- `JsonBody.Companion.fromAssets(...)` delegates to asset loading.
- Default charset is UTF-8.
