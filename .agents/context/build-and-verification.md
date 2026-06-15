# Build And Verification

## Shell

- В этом проекте предпочитай запускать команды через `rtk`.
- Для поиска файлов используй `rg --files`, для поиска текста - `rg`.
- Не читай `.gradle` напрямую.

## Useful commands

Проверка новых MolApi modules:

```bash
rtk ./gradlew :molapi-core:testAndroidHostTest :molapi-http:testAndroidHostTest
rtk ./gradlew :molapi-core:iosSimulatorArm64Test :molapi-http:iosSimulatorArm64Test
rtk ./gradlew :molapi-http-ktor:testAndroidHostTest
rtk ./gradlew :molapi-http-ktor:iosSimulatorArm64Test
```

Проверка sample module:

```bash
rtk ./gradlew :sample:testAndroidHostTest
rtk ./gradlew :sample:iosSimulatorArm64Test
```

Android app build:

```bash
rtk ./gradlew :sampleAndroidApp:assembleDebug
```

## Verification rules

- Запускай минимальную релевантную проверку после изменения кода.
- Для Gradle/KMP wiring проверяй хотя бы один Android host test task и один iOS simulator task,
  если изменение влияет на common code.
- Если команда упала из-за sandbox-доступа к Gradle cache или Kotlin/Native cache, повтори с
  escalation.
- Если проверку выполнить нельзя, явно укажи причину и что осталось непроверенным.

## Git

- Не используй destructive команды без явного запроса пользователя.
- Если `git status` недоступен из текущего каталога, не делай выводов о чистоте worktree.
- Не откатывай изменения, которые не делал сам.
