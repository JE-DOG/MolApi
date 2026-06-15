# KMP Implementation Rules

## Common code

- Код в `commonMain` должен быть Kotlin Multiplatform compatible.
- Не используй JVM-only API в common code.
- Не добавляй `kotlinx.coroutines`, если задача не требует асинхронности.
- Не добавляй внешние зависимости без необходимости.
- Используй стандартную библиотеку Kotlin, если этого достаточно.

## Gradle

- В KMP library modules используется прямое применение aliases из version catalog:
  `libs.plugins.kotlinMultiplatform` и `libs.plugins.androidMultiplatformLibrary`.
- Для тестов в `commonTest` используется `implementation(libs.kotlin.test)`.
- Для Android host tests в Android KMP library включай `withHostTest {}` внутри `androidLibrary`.
- Для зависимости `molapi-http` от core используй `api(project(":molapi-core"))`, потому что
  публичные HTTP-типы наследуются от core API.
- Для `molapi-http-ktor` используй `implementation(project(":molapi-http"))`,
  `implementation(project(":molapi-core"))` и `implementation(libs.ktor.client.core)`.
- Для тестов Ktor plugin можно использовать `libs.ktor.client.mock` и
  `libs.kotlinx.coroutines.test`, потому что Ktor client API suspend-based.

## Dependency sources

- Не обращайся напрямую к `.gradle`.
- Если нужно изучить исходники зависимости, начинай с `ksrc --help`, затем используй `ksrc search`
  и `ksrc cat`.

## Style

- Имена тестовых классов: `*Test`.
- Имена тестовых функций: `lowerCamelCase`, без backtick-имен, если существующий стиль не изменится.
- Assertions из `kotlin.test`: `assertEquals`, `assertTrue`, `assertFalse`, `assertNull` и т.п.
