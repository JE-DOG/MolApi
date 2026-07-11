---
name: molapi-room-implementation
description: Implement, modify, or review the MolApi Room Kotlin Multiplatform persistence module. Use when a task touches molapi-room, dag.khinkal.molapi.room, RoomApiMockRegistry, ApiMockParser, MolApiRoomDatabase, DAO/entity/storage code, Room schemas, KSP Room Gradle wiring, or Room registry tests.
---

# MolApi Room Implementation

## Scope

Work on `molapi-room`, the generic persistent registry implementation backed by Room KMP.

## Required Context

Read before editing:

- `AGENTS.md`
- `.agents/context/role-and-quality.md`
- `.agents/context/project-structure.md`
- `.agents/context/kmp-implementation-rules.md`
- `.agents/context/molapi-contract.md`
- `.agents/context/build-and-verification.md`

Inspect current local files:

- `molapi-room/build.gradle.kts`
- `molapi-room/src/commonMain/kotlin/dag/khinkal/molapi/room/**`
- `molapi-room/schemas/**`
- `sample/src/commonMain/kotlin/dag/khinkal/molapi/RoomHttpMockRegistry.kt` if sample parser wiring
  is relevant

Use `ksrc --help` before inspecting Room/KSP dependency sources.

## Contract

Preserve generic persistence:

- `ApiMockParser<Request, Matcher, Response, Id>` owns encode/decode of matcher, response, and
  stored records.
- `RoomApiMockRegistry` implements core `ApiMockRegistry`.
- Stored ids are string records; parser logic is responsible for reconstructing typed mocks.
- `add(response, matcher)` generates id through `idGenerator`.
- `remove(id)` removes by string id representation.
- `clear()` clears storage.
- `find(request)` returns the first decoded mock whose matcher matches.

## Implementation Rules

- Keep code multiplatform through Room KMP source sets.
- Keep Room schema output under `molapi-room/schemas`.
- Keep KSP dependencies for Android and iOS targets aligned with Gradle.
- Do not specialize Room module for HTTP; HTTP parser examples belong in sample or a separate
  adapter.
- Do not change current blocking behavior such as `find` using `runBlocking` unless explicitly
  requested.

## Tests

Cover:

- add/find/remove/clear persistence behavior
- parser encode/decode integration
- duplicate id behavior
- `mocks` state updates
- schema changes when database structure changes

If schema output changes, inspect and explain the schema diff.

## Validation

Run:

```bash
rtk ./gradlew :molapi-room:testAndroidHostTest :molapi-room:iosSimulatorArm64Test
```

If sample Room registry wiring changes, also run sample checks through `$molapi-change-validation`.
