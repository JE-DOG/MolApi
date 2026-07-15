---
name: molapi-room-implementation
description: Implement, modify, or review the MolApi Room Kotlin Multiplatform persistence module. Use when a task touches molapi-room, dag.khinkal.molapi.room, RoomApiMockRegistry, ApiMockParser, RoomApiMockRecord, String mock ids, StateFlow-backed persistent registry state, MolApiRoomDatabase, DAO/entity/storage code, Room schemas, KSP Room Gradle wiring, or Room registry tests.
---

# MolApi Room Implementation

## Scope

Work on `molapi-room`, the generic persistent registry implementation backed by Room KMP.

## Contract

Preserve generic persistence:

- `ApiMockParser<Request, Matcher, Response>` encodes and decodes matcher and response values as
  strings; it does not parse ids or whole records.
- `RoomApiMockRegistry` implements core `ApiMockRegistry`.
- `RoomApiMockRecord` stores `id`, encoded matcher, and encoded response as strings.
- Mock ids are always `String`; the default `UuidApiMockIdGenerator` creates UUID strings.
- `RoomApiMockRegistry.mocks` exposes decoded storage updates as a read-only `StateFlow`.
- `add(response, matcher)` generates id through `idGenerator`.
- `remove(id: String)` removes that stored record and reports whether a row was deleted.
- `clear()` clears storage.
- `find(request)` currently reads storage through `runBlocking` and returns the first decoded mock
  whose matcher matches.
