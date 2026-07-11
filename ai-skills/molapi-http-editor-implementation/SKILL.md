---
name: molapi-http-editor-implementation
description: Implement, modify, or review the MolApi HTTP editor Compose Multiplatform module. Use when a task touches molapi-http-editor, dag.khinkal.molapi.http.editor, HttpMockEditorScreen, HttpMockEditorState, Compose resources, Navigation3 usage, editor Gradle wiring, editor common tests, or editor UI/state behavior.
---

# MolApi HTTP Editor Implementation

## Scope

Work on `molapi-http-editor`, the Compose Multiplatform editor UI for `HttpApiMockRegistry`.

## Required Context

Read before editing:

- `AGENTS.md`
- `.agents/context/role-and-quality.md`
- `.agents/context/project-structure.md`
- `.agents/context/kmp-implementation-rules.md`
- `.agents/context/build-and-verification.md`

Inspect current local files:

- `molapi-http-editor/build.gradle.kts`
- `molapi-http-editor/src/commonMain/kotlin/dag/khinkal/molapi/http/editor/**`
- `molapi-http-editor/src/commonMain/composeResources/**`
- `molapi-http-editor/src/commonTest/**`

## Contract

Preserve editor responsibilities:

- `HttpMockEditorScreen(registry, modifier)` is the public UI entry point.
- State logic should remain in `HttpMockEditorState` where possible.
- UI edits create, filter, show, remove, and clear HTTP mocks through `HttpApiMockRegistry`.
- Visible strings should come from compose resources.

## Implementation Rules

- Keep UI code multiplatform; do not add Android-only APIs to common composables.
- Keep public composables explicit and stable.
- Do not put persistence concerns here; Room has its own module.
- Do not implement new HTTP matching semantics in the editor; use `molapi-http` contracts.
- Keep Compose layouts practical and test state/parsing logic outside UI when possible.

## Tests

Prefer common tests for state logic:

- draft validation
- header parsing errors
- status code parsing
- create/remove/clear behavior
- filtering behavior

Run UI/compile checks when layout or resource wiring changes.

## Validation

Run:

```bash
rtk ./gradlew :molapi-http-editor:testAndroidHostTest :molapi-http-editor:iosSimulatorArm64Test
```

If sample embeds the editor, also run sample checks through `$molapi-change-validation`.
