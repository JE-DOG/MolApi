# Project Structure

## Репозиторий

`MolApi` - Kotlin Multiplatform проект для Android и iOS.

Основные директории:

- `sampleAndroidApp/` - Android application module.
- `iosApp/SampleIos/` - iOS entry point.
- `sample/` - sample Compose Multiplatform приложение.
- `molapi-core/` - core-модуль библиотеки мокирования API.
- `molapi-http/` - HTTP-специализация библиотеки мокирования API.
- `molapi-http-ktor/` - Ktor client plugin и адаптеры для `molapi-http`.
- `gradle/libs.versions.toml` - version catalog.

## Gradle-модули

В `settings.gradle.kts` подключены:

- `:sampleAndroidApp`
- `:sample`
- `:molapi-core`, физический путь `molapi-core`
- `:molapi-http`, физический путь `molapi-http`
- `:molapi-http-ktor`, физический путь `molapi-http-ktor`

Важно: физические пути находятся в корне и совпадают с Gradle project names без двоеточия:
`molapi-core`, `molapi-http`, `molapi-http-ktor` и т.д. Вложенные имена `:molapi:core`
и `:molapi:http` конфликтуют с root name `MolApi` при генерации type-safe project accessors.

## Пакеты

- Core package: `dag.khinkal.molapi.core`
- HTTP package: `dag.khinkal.molapi.http`
- Ktor HTTP package: `dag.khinkal.molapi.http.ktor`
- Sample application package: `dag.khinkal.molapi`

## Source sets

Ожидаемые KMP source sets:

- `src/commonMain/kotlin`
- `src/commonTest/kotlin`
- `src/androidMain/kotlin`
- `src/androidHostTest/kotlin`
- `src/iosMain/kotlin`
- `src/iosTest/kotlin`

Новые common API должны сначала жить в `commonMain`, если им не нужны платформенные API.
