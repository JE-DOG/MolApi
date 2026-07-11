---
name: molapi-change-validation
description: Validate MolApi repository changes and choose focused Gradle checks. Use when Codex needs to verify changes in molapi-core, molapi-http, molapi-http-ktor, molapi-http-retrofit, molapi-http-gson, molapi-http-serialization, molapi-http-android-assets, molapi-http-editor, molapi-room, sample, sampleAndroidApp, Gradle wiring, source sets, or project configuration.
---

# MolApi Change Validation

## Scope

Use this skill after implementing changes or when the user asks what to run. It does not replace
module-specific skills; it turns changed paths into a minimal relevant validation set.

## Required Context

Read before validating:

- `AGENTS.md`
- `.agents/context/build-and-verification.md`
- `.agents/context/project-structure.md` when Gradle/source-set wiring changed
- `.agents/context/kmp-implementation-rules.md` when common or multiplatform code changed

## Workflow

1. Inspect changed files:

```bash
rtk git status --short
rtk git diff --name-only
```

2. Run the planner:

```bash
rtk env PYTHONDONTWRITEBYTECODE=1 python3 ai-skills/molapi-change-validation/scripts/validation_plan.py
```

3. Review the suggested commands before running them.
4. Add extra checks when a public contract, sample integration, or Gradle configuration has indirect
   effects.
5. Report only checks actually run in the current task.

## Command Matrix

Common KMP modules:

```bash
rtk ./gradlew :molapi-core:testAndroidHostTest :molapi-core:iosSimulatorArm64Test
rtk ./gradlew :molapi-http:testAndroidHostTest :molapi-http:iosSimulatorArm64Test
rtk ./gradlew :molapi-http-ktor:testAndroidHostTest :molapi-http-ktor:iosSimulatorArm64Test
rtk ./gradlew :molapi-http-serialization:testAndroidHostTest :molapi-http-serialization:iosSimulatorArm64Test
rtk ./gradlew :molapi-http-editor:testAndroidHostTest :molapi-http-editor:iosSimulatorArm64Test
rtk ./gradlew :molapi-room:testAndroidHostTest :molapi-room:iosSimulatorArm64Test
```

Android-only modules:

```bash
rtk ./gradlew :molapi-http-gson:testAndroidHostTest
rtk ./gradlew :molapi-http-android-assets:testAndroidHostTest
rtk ./gradlew :molapi-http-retrofit:testAndroidHostTest
```

Sample:

```bash
rtk ./gradlew :sample:testAndroidHostTest :sample:iosSimulatorArm64Test
rtk ./gradlew :sampleAndroidApp:assembleDebug
```

## Review Checklist

- Public declarations remain explicit under `explicitApi()`.
- Common code has no Android/JVM-only imports.
- Android-only integrations stay in `androidMain`.
- New dependencies are necessary and scoped narrowly.
- Tests cover changed behavior, not only implementation details.
- No secrets or MCP credentials were added.
- `.gradle` was not read directly; use `ksrc` for dependency source inspection.

## Sandbox Notes

If Gradle fails because of sandbox access to Gradle, Android, Kotlin/Native, or KSP caches, rerun
the same command with the environment's required escalation flow.
