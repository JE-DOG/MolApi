---
name: molapi-http-editor-implementation
description: Implement, modify, or review the MolApi HTTP editor Compose Multiplatform module. Use when a task touches molapi-http-editor, dag.khinkal.molapi.http.editor, MolApiEditor, MolApiHttpMockEditorScreen, HttpMockEditorViewModel, HttpRoomApiMockParser, Room-backed editor initialization, Compose resources, platform document pickers, editor Gradle wiring, tests, or UI/state behavior.
---

# MolApi HTTP Editor Implementation

## Scope

`:molapi-http-editor` exposes a Compose Multiplatform `MolApiHttpMockEditorScreen` and a
`MolApiEditor` facade. The facade owns one shared `HttpApiMockRegistry` so the editor UI and the
network interceptor/plugin see the same mocks.

## Contract

Preserve editor responsibilities:

- `MolApiHttpMockEditorScreen(registry: () -> HttpApiMockRegistry = MolApiEditor::registry,
  modifier)` is the public UI entry point.
- `MolApiEditor` owns the shared registry and must be initialized before its default registry is
  read. Preserve initialization from an explicit registry or `MolApiRoomDatabase`.
- Platform `MolApiEditor.init` extensions construct the default Room database for Android and iOS.
- State and draft behavior belong in `HttpMockEditorViewModel`; the internal
  `HttpMockEditorScreen` renders that state.
- `HttpRoomApiMockParser` is the editor's internal HTTP matcher/response persistence adapter.
- UI edits create, filter, show, remove, and clear HTTP mocks through `HttpApiMockRegistry`.
- Request and response JSON bodies can be loaded through platform document pickers.

## Usage example

Initialize the editor once at application startup.

Example for Android:

```kotlin
import android.app.Application
import dag.khinkal.molapi.http.editor.MolApiEditor
import dag.khinkal.molapi.http.editor.init

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        MolApiEditor.init(this)
    }
}
```

Example for iOS:

```kotlin
import dag.khinkal.molapi.http.editor.MolApiEditor
import dag.khinkal.molapi.http.editor.init

fun initializeMolApiEditor() {
    MolApiEditor.init()
}
```

Call the exported `initializeMolApiEditor()` from the Swift `App.init()` or another iOS application
startup point before creating screens or HTTP clients.

```kotlin
import androidx.compose.runtime.Composable
import dag.khinkal.molapi.http.editor.MolApiHttpMockEditorScreen

@Composable
fun MockEditor() {
    MolApiHttpMockEditorScreen()
}
```

For tests or custom dependency injection, pass an explicit registry provider:

```kotlin
import dag.khinkal.molapi.http.editor.MolApiEditor
import dag.khinkal.molapi.http.ktor.plugin.MolApiKtorPlugin
import io.ktor.client.HttpClient

val customRegistry: HttpApiMockRegistry = CustomHttpApiMockRegistry() 

val client = HttpClient {
  install(MolApiKtorPlugin) {
    registry = customRegistry
  }
}
// Or if Retrofit
val okHttpClient = OkHttpClient.Builder()
  .addMolApiInterceptor(customRegistry)
  .build()

@Composable
fun TestScreen() {
  MolApiHttpMockEditorScreen(registry = { customRegistry })
}
```
