# MolApi

MolApi is a Kotlin Multiplatform toolkit for registering API mocks and resolving incoming requests
against them. The core idea is intentionally small:

```text
request -> registry.find(request) -> mock.matcher.matches(request) -> mock.response
```

The project currently targets Android and iOS for the common library modules, with additional
Android-only adapters where the underlying stack is Android/JVM-only.

## Modules

| Module                        | Purpose                                                                        | Targets      |
|-------------------------------|--------------------------------------------------------------------------------|--------------|
| `:molapi-core`                | Generic request, matcher, response and registry contracts.                     | Android, iOS |
| `:molapi-http`                | HTTP request/response model, matchers, in-memory registry aliases and DSL.     | Android, iOS |
| `:molapi-http-ktor`           | Ktor Client plugin that returns a mock response before a real request is sent. | Android, iOS |
| `:molapi-http-serialization`  | `kotlinx.serialization` helpers for JSON HTTP bodies.                          | Android, iOS |
| `:molapi-room`                | Room-backed persistent registry foundation.                                    | Android, iOS |
| `:molapi-http-editor`         | Compose Multiplatform screen for viewing, searching and editing HTTP mocks.    | Android, iOS |
| `:molapi-http-android-assets` | Helps create JSON HTTP bodies from Android project assets.                     | Android      |
| `:molapi-http-gson`           | Gson helpers for JSON HTTP bodies.                                             | Android      |
| `:molapi-http-retrofit`       | OkHttp/Retrofit interceptor integration.                                       | Android      |
| `:sample`                     | Shared sample app code.                                                        | Android, iOS |
| `:sampleAndroidApp`           | Android sample application entry point.                                        | Android      |
| `SampleIosApp/`               | iOS sample application entry point.                                            | iOS          |

## HTTP Mocking

Create an HTTP registry and register mocks through the DSL:

```kotlin
import dag.khinkal.molapi.http.dsl.get
import dag.khinkal.molapi.http.dsl.post
import dag.khinkal.molapi.http.model.Headers
import dag.khinkal.molapi.http.model.JsonBody
import dag.khinkal.molapi.http.registry.HttpInMemoryApiMockRegistry

val registry = HttpInMemoryApiMockRegistry().apply {
    get(
        url = "/todos",
        json = """[{"id":1,"title":"from molapi","completed":false}]""",
    )

    post(
        url = "/todos",
        headers = Headers.jsonContent(),
        body = JsonBody("""{"title":"new"}"""),
        json = """{"id":2,"title":"new","completed":false}""",
        statusCode = 201,
    )
}
```

The HTTP DSL supports `get`, `post`, `put`, `patch`, `head`, `delete`, and generic `http` mocks.
Each registered mock stores a matcher and a response:

- URL matching uses `request.url.contains(configuredUrl)`.
- Method, headers and body matching use exact equality when those fields are provided.
- If a matcher field is `null`, that field is ignored.
- The registry returns the first matching mock.

## Ktor Client

Install `MolApiKtorPlugin` into a Ktor `HttpClient`. If a registered mock matches the outgoing
request, the plugin returns the mock response. If no mock matches, the request continues normally.

```kotlin
import dag.khinkal.molapi.http.ktor.plugin.MolApiKtorPlugin
import dag.khinkal.molapi.http.registry.HttpInMemoryApiMockRegistry
import io.ktor.client.HttpClient

val registry = HttpInMemoryApiMockRegistry()

val client = HttpClient {
    install(MolApiKtorPlugin) {
        this.registry = registry
    }
}
```

`sample/src/commonMain/kotlin/dag/khinkal/molapi/TodoApi.kt` shows the same pattern in the sample
client configuration.

## Retrofit and OkHttp

On Android, add `MolApiRetrofitInterceptor` through the OkHttp builder used by Retrofit:

```kotlin
import dag.khinkal.molapi.http.registry.HttpInMemoryApiMockRegistry
import dag.khinkal.molapi.http.retrofit.interceptor.addMolApiInterceptor
import okhttp3.OkHttpClient

val registry = HttpInMemoryApiMockRegistry()

val okHttpClient = OkHttpClient.Builder()
    .addMolApiInterceptor(registry)
    .build()
```

The interceptor follows the same fallback rule as the Ktor plugin: mock match first, real request
otherwise.

## JSON Helpers

Use `molapi-http-serialization` in multiplatform code:

```kotlin
import dag.khinkal.molapi.http.serializable.util.JsonHttpResponse
import kotlinx.serialization.Serializable

@Serializable
data class TodoDto(
    val id: Int,
    val title: String,
)

val response = JsonHttpResponse(
    serializableBody = TodoDto(id = 1, title = "from molapi"),
)
```

Use `molapi-http-gson` in Android code:

```kotlin
import dag.khinkal.molapi.http.gson.util.GsonHttpResponse
import dag.khinkal.molapi.http.model.Headers

val response = GsonHttpResponse(
    headers = Headers.jsonContent(),
    body = TodoDto(id = 1, title = "from molapi"),
)
```

## Android Assets

Use `molapi-http-android-assets` when mock bodies are stored in Android project assets. The module
only creates `JsonBody` values from asset files; request matching, response status and headers stay
configured through the regular `molapi-http` API.

```kotlin
import android.content.Context
import dag.khinkal.molapi.http.assets.AssetJsonBody
import dag.khinkal.molapi.http.dsl.post
import dag.khinkal.molapi.http.model.Headers
import dag.khinkal.molapi.http.registry.HttpInMemoryApiMockRegistry
import android.content.res.AssetManager

fun createRegistry(
    context: Context,
    assetManager: AssetManager,
): HttpInMemoryApiMockRegistry =
    HttpInMemoryApiMockRegistry().apply {
        post(
            url = "/todos",
            headers = Headers.jsonContent(),
            body = AssetJsonBody(context, "requests/create-todo.json"),
            response = JsonBody.fromAssets(context, "responses/todo-created.json"),
            responseHeaders = Headers.jsonContent(),
            statusCode = 201,
        )
        post(
            url = "/todos",
            headers = Headers.jsonContent(),
            body = AssetJsonBody(assetManager, "requests/create-todo.json"),
            response = JsonBody.fromAssets(assetManager, "responses/todo-created.json"),
            responseHeaders = Headers.jsonContent(),
            statusCode = 201,
        )
    }
```

`AssetJsonBody(context, path)` and `JsonBody.fromAssets(context, path)` read the asset text as UTF-8
by default. Pass a `Charset` explicitly if an asset uses another encoding.

## Persistent Registry

`:molapi-room` provides `RoomApiMockRegistry`, which stores mocks in Room and restores them through
an `ApiMockParser`. The parser owns serialization for matcher and response types, so a feature can
decide which mock shapes are safe to persist.

The sample contains an HTTP parser in
`sample/src/commonMain/kotlin/dag/khinkal/molapi/RoomHttpMockRegistry.kt`.

## Editor UI

`:molapi-http-editor` exposes a Compose Multiplatform `HttpMockEditorScreen`:

```kotlin
import androidx.compose.runtime.Composable
import dag.khinkal.molapi.http.editor.HttpMockEditorScreen
import dag.khinkal.molapi.http.registry.HttpApiMockRegistry
import dag.khinkal.molapi.http.registry.HttpInMemoryApiMockRegistry

@Composable
fun MockEditor(
    registry: HttpApiMockRegistry = HttpInMemoryApiMockRegistry(),
) {
    HttpMockEditorScreen(registry = registry)
}
```

The screen can list registered HTTP mocks, filter them, add new JSON mocks and clear or remove
entries from the registry.

## Running

Build the Android sample:

```bash
./gradlew :sampleAndroidApp:assembleDebug
```

Run focused library checks:

```bash
./gradlew :molapi-core:testAndroidHostTest :molapi-http:testAndroidHostTest
./gradlew :molapi-http-ktor:testAndroidHostTest
./gradlew :molapi-http-android-assets:testAndroidHostTest
./gradlew :sample:testAndroidHostTest
```

Run iOS simulator checks for common modules:

```bash
./gradlew :molapi-core:iosSimulatorArm64Test :molapi-http:iosSimulatorArm64Test
./gradlew :molapi-http-ktor:iosSimulatorArm64Test
./gradlew :sample:iosSimulatorArm64Test
```

The iOS app entry point is in `SampleIosApp/`; open that directory in Xcode to run the sample on an
iOS
simulator or device.

## Development Notes

- Public APIs use explicit Kotlin visibility.
- Common modules avoid platform-only APIs in `commonMain`.
- `:molapi-http` depends on `:molapi-core` as API because HTTP public types expose core contracts.
