---
name: molapi-http-android-assets-implementation
description: Implement, modify, or review the MolApi Android assets HTTP helper module. Use when a task touches molapi-http-android-assets, dag.khinkal.molapi.http.assets, AssetJsonBody, JsonBody.fromAssets, Android resource/test asset Gradle wiring, Robolectric tests, or Android host tests for asset-backed JSON mocks.
---

# MolApi Android Assets Implementation

## Scope

Work on `molapi-http-android-assets`, the Android-only helper module that loads `JsonBody` from
Android assets.

## Required Context

Read before editing:

- `AGENTS.md`
- `.agents/context/role-and-quality.md`
- `.agents/context/project-structure.md`
- `.agents/context/kmp-implementation-rules.md`
- `.agents/context/build-and-verification.md`

Inspect current local files:

- `molapi-http-android-assets/build.gradle.kts`
- `molapi-http-android-assets/src/androidMain/kotlin/dag/khinkal/molapi/http/assets/**`
- `molapi-http-android-assets/src/androidHostTest/**`

## Contract

Preserve helper shape:

- `AssetJsonBody(context, path, charset)` loads from `context.assets`.
- `AssetJsonBody(assetManager, path, charset)` loads from `AssetManager`.
- `JsonBody.Companion.fromAssets(...)` delegates to asset loading.
- Default charset is UTF-8.

## Implementation Rules

- Keep code in `androidMain`.
- Keep tests in `androidHostTest`.
- Enable Android resources in the module.
- Include Android resources in host tests when needed.
- Use Robolectric for Android framework/resource access.
- Do not add common/iOS support.
- Do not introduce file-system based loaders into this Android assets module unless explicitly
  requested.

## Tests

Cover:

- loading existing asset content
- missing path failure behavior if changed
- charset behavior when relevant
- companion helper delegation
- response integration only if the task changes HTTP usage

Keep JSON fixtures under test assets and avoid large fixtures unless the feature needs them.

## Validation

Run:

```bash
rtk ./gradlew :molapi-http-android-assets:testAndroidHostTest
```

If sample Android usage changes, also run sample Android checks through `$molapi-change-validation`.
