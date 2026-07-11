#!/usr/bin/env python3
"""Print suggested MolApi validation commands for changed paths."""

from __future__ import annotations

import subprocess
import sys
from pathlib import Path


COMMON_MODULES = {
    "molapi-core",
    "molapi-http",
    "molapi-http-ktor",
    "molapi-http-serialization",
    "molapi-http-editor",
    "molapi-room",
}

ANDROID_ONLY_MODULES = {
    "molapi-http-gson",
    "molapi-http-android-assets",
    "molapi-http-retrofit",
}

ROOT_GRADLE_FILES = {
    "settings.gradle.kts",
    "build.gradle.kts",
    "gradle/libs.versions.toml",
    "gradle.properties",
}


def changed_paths_from_git() -> list[str]:
    result = subprocess.run(
        ["git", "diff", "--name-only", "HEAD"],
        check=False,
        text=True,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
    )
    if result.returncode != 0:
        print(result.stderr.strip(), file=sys.stderr)
        return []
    return [line.strip() for line in result.stdout.splitlines() if line.strip()]


def module_for_path(path: str) -> str | None:
    first = Path(path).parts[0] if Path(path).parts else ""
    if first in COMMON_MODULES or first in ANDROID_ONLY_MODULES:
        return first
    if first in {"sample", "sampleAndroidApp"}:
        return first
    return None


def command_for_common_module(module: str) -> str:
    return f"rtk ./gradlew :{module}:testAndroidHostTest :{module}:iosSimulatorArm64Test"


def command_for_android_module(module: str) -> str:
    return f"rtk ./gradlew :{module}:testAndroidHostTest"


def build_commands(paths: list[str]) -> list[str]:
    modules = {module_for_path(path) for path in paths}
    modules.discard(None)

    commands: list[str] = []
    has_root_gradle_change = any(path in ROOT_GRADLE_FILES for path in paths)

    if has_root_gradle_change:
        commands.extend(
            [
                "rtk ./gradlew :molapi-core:testAndroidHostTest :molapi-http:testAndroidHostTest :molapi-http-ktor:testAndroidHostTest",
                "rtk ./gradlew :molapi-core:iosSimulatorArm64Test :molapi-http:iosSimulatorArm64Test :molapi-http-ktor:iosSimulatorArm64Test",
            ]
        )

    for module in sorted(modules & COMMON_MODULES):
        commands.append(command_for_common_module(module))

    for module in sorted(modules & ANDROID_ONLY_MODULES):
        commands.append(command_for_android_module(module))

    if "sample" in modules:
        commands.append("rtk ./gradlew :sample:testAndroidHostTest :sample:iosSimulatorArm64Test")

    if "sampleAndroidApp" in modules:
        commands.append("rtk ./gradlew :sampleAndroidApp:assembleDebug")

    if "sample" in modules and (
        modules & (COMMON_MODULES | ANDROID_ONLY_MODULES) or has_root_gradle_change
    ):
        commands.append("rtk ./gradlew :sampleAndroidApp:assembleDebug")

    return deduplicate(commands)


def deduplicate(values: list[str]) -> list[str]:
    seen: set[str] = set()
    result: list[str] = []
    for value in values:
        if value not in seen:
            seen.add(value)
            result.append(value)
    return result


def main() -> int:
    paths = sys.argv[1:] or changed_paths_from_git()
    if not paths:
        print("No changed paths detected. Pass paths explicitly to plan validation.")
        return 0

    print("Changed paths:")
    for path in paths:
        print(f"- {path}")

    commands = build_commands(paths)
    if not commands:
        print("\nNo module-specific commands inferred. Inspect the change and choose checks manually.")
        return 0

    print("\nSuggested validation commands:")
    for command in commands:
        print(command)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
