# MolApi

![Apache Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?logo=Apache%20Maven&logoColor=white)
![Kotlin](https://camo.githubusercontent.com/7b0f7d398c2fcf5ae755c98c3d9760c3051386aa52c331e4da829018dfdfc74c/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f4b6f746c696e2d3746353246463f7374796c653d666f722d7468652d6261646765266c6f676f3d6b6f746c696e266c6f676f436f6c6f723d7768697465)

MolApi is a Kotlin Multiplatform toolkit for registering API mocks and resolving incoming requests
against them. The core idea is intentionally small:

## AI Skills

The repository includes project-specific ai skills in [`ai-skills/`](ai-skills/). They provide
implementation guidance for each MolApi library module

```text
request -> registry.find(request) -> mock.matcher.matches(request) -> mock.response
```

The project currently targets Android and iOS for the common library modules, with additional
Android-only adapters where the underlying stack is Android/JVM-only.

## Installation

MolApi artifacts are published to Maven Central under the `io.github.je-dog` group. Add
`mavenCentral()` to your repositories:

```kotlin
repositories {
    mavenCentral()
}
```

Then add only the modules your project needs. For a Kotlin Multiplatform project, keep common
modules in `commonMain` and Android-only adapters in `androidMain`:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.je-dog:molapi-http:<latest_version>")
            implementation("io.github.je-dog:molapi-http-ktor:<latest_version>")
            implementation("io.github.je-dog:molapi-http-serialization:<latest_version>")
        }

        androidMain.dependencies {
            implementation("io.github.je-dog:molapi-http-retrofit:<latest_version>")
            implementation("io.github.je-dog:molapi-http-gson:<latest_version>")
            implementation("io.github.je-dog:molapi-http-android-assets:<latest_version>")
        }
    }
}
```

For a regular Android Gradle module, add dependencies through the usual `dependencies` block:

```kotlin
dependencies {
    implementation("io.github.je-dog:molapi-http:<latest_version>")
    implementation("io.github.je-dog:molapi-http-retrofit:<latest_version>")
}
```

## Modules

<div style="overflow-x: auto;">
<table>
  <thead>
    <tr>
      <th>Module</th>
      <th>Maven Central</th>
      <th>Purpose</th>
      <th>Targets</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><code>:molapi-core</code></td>
      <td><a href="https://central.sonatype.com/artifact/io.github.je-dog/molapi-core"><img alt="Maven Central: molapi-core" src="https://img.shields.io/maven-central/v/io.github.je-dog/molapi-core?label=molapi-core"></a></td>
      <td>Generic request, matcher, response and registry contracts.</td>
      <td>Android, iOS</td>
    </tr>
    <tr>
      <td><a href="#http-mocking"><code>:molapi-http</code></a></td>
      <td><a href="https://central.sonatype.com/artifact/io.github.je-dog/molapi-http"><img alt="Maven Central: molapi-http" src="https://img.shields.io/maven-central/v/io.github.je-dog/molapi-http?label=molapi-http"></a></td>
      <td>HTTP request/response model, matchers, in-memory registry aliases and DSL.</td>
      <td>Android, iOS</td>
    </tr>
    <tr>
      <td><a href="#ktor-client"><code>:molapi-http-ktor</code></a></td>
      <td><a href="https://central.sonatype.com/artifact/io.github.je-dog/molapi-http-ktor"><img alt="Maven Central: molapi-http-ktor" src="https://img.shields.io/maven-central/v/io.github.je-dog/molapi-http-ktor?label=molapi-http-ktor"></a></td>
      <td>Ktor Client plugin that returns a mock response before a real request is sent.</td>
      <td>Android, iOS</td>
    </tr>
    <tr>
      <td><a href="#json-helpers"><code>:molapi-http-serialization</code></a></td>
      <td><a href="https://central.sonatype.com/artifact/io.github.je-dog/molapi-http-serialization"><img alt="Maven Central: molapi-http-serialization" src="https://img.shields.io/maven-central/v/io.github.je-dog/molapi-http-serialization?label=molapi-http-serialization"></a></td>
      <td><code>kotlinx.serialization</code> helpers for JSON HTTP bodies.</td>
      <td>Android, iOS</td>
    </tr>
    <tr>
      <td><a href="#persistent-registry"><code>:molapi-room</code></a></td>
      <td><a href="https://central.sonatype.com/artifact/io.github.je-dog/molapi-room"><img alt="Maven Central: molapi-room" src="https://img.shields.io/maven-central/v/io.github.je-dog/molapi-room?label=molapi-room"></a></td>
      <td>Room-backed persistent registry foundation.</td>
      <td>Android, iOS</td>
    </tr>
    <tr>
      <td><a href="#editor-ui"><code>:molapi-http-editor</code></a></td>
      <td><a href="https://central.sonatype.com/artifact/io.github.je-dog/molapi-http-editor"><img alt="Maven Central: molapi-http-editor" src="https://img.shields.io/maven-central/v/io.github.je-dog/molapi-http-editor?label=molapi-http-editor"></a></td>
      <td>Compose Multiplatform screen for viewing, searching and editing HTTP mocks.</td>
      <td>Android, iOS</td>
    </tr>
    <tr>
      <td><a href="#android-assets"><code>:molapi-http-android-assets</code></a></td>
      <td><a href="https://central.sonatype.com/artifact/io.github.je-dog/molapi-http-android-assets"><img alt="Maven Central: molapi-http-android-assets" src="https://img.shields.io/maven-central/v/io.github.je-dog/molapi-http-android-assets?label=molapi-http-android-assets"></a></td>
      <td>Helps create JSON HTTP bodies from Android project assets.</td>
      <td>Android</td>
    </tr>
    <tr>
      <td><a href="#json-helpers"><code>:molapi-http-gson</code></a></td>
      <td><a href="https://central.sonatype.com/artifact/io.github.je-dog/molapi-http-gson"><img alt="Maven Central: molapi-http-gson" src="https://img.shields.io/maven-central/v/io.github.je-dog/molapi-http-gson?label=molapi-http-gson"></a></td>
      <td>Gson helpers for JSON HTTP bodies.</td>
      <td>Android</td>
    </tr>
    <tr>
      <td><a href="#retrofit-and-okhttp"><code>:molapi-http-retrofit</code></a></td>
      <td><a href="https://central.sonatype.com/artifact/io.github.je-dog/molapi-http-retrofit"><img alt="Maven Central: molapi-http-retrofit" src="https://img.shields.io/maven-central/v/io.github.je-dog/molapi-http-retrofit?label=molapi-http-retrofit"></a></td>
      <td>OkHttp/Retrofit interceptor integration.</td>
      <td>Android</td>
    </tr>
    <tr>
      <td><code>:sample</code></td>
      <td>Not published</td>
      <td>Shared sample app code.</td>
      <td>Android, iOS</td>
    </tr>
    <tr>
      <td><code>:sampleAndroidApp</code></td>
      <td>Not published</td>
      <td>Android sample application entry point.</td>
      <td>Android</td>
    </tr>
    <tr>
      <td><code>SampleIosApp/</code></td>
      <td>Not published</td>
      <td>iOS sample application entry point.</td>
      <td>iOS</td>
    </tr>
  </tbody>
</table>
</div>

## HTTP Mocking

Create an HTTP registry and register mocks through the DSL. The main response overloads are shown
below; `get`, `post`, `put`, `patch`, `head`, and `delete` have these response forms, while
generic `http` also accepts an explicit `HttpMethod`:

```kotlin
import dag.khinkal.molapi.http.dsl.get
import dag.khinkal.molapi.http.dsl.http
import dag.khinkal.molapi.http.dsl.patch
import dag.khinkal.molapi.http.dsl.post
import dag.khinkal.molapi.http.dsl.put
import dag.khinkal.molapi.http.model.Headers
import dag.khinkal.molapi.http.model.HttpMethod
import dag.khinkal.molapi.http.model.HttpResponse
import dag.khinkal.molapi.http.model.HttpUrl
import dag.khinkal.molapi.http.model.JsonBody
import dag.khinkal.molapi.http.registry.HttpInMemoryApiMockRegistry

val registry = HttpInMemoryApiMockRegistry().apply {
    // A JSON string shortcut.
    get(
        path = "/todos",
        json = """[{"id":1,"title":"from molapi","completed":false}]""",
    )

    // A full response, including custom headers and status.
    post(
        url = HttpUrl(path = "/todos"),
        headers = Headers.jsonContent(),
        body = JsonBody("""{"title":"new"}"""),
        response = HttpResponse(
            body = JsonBody("""{"id":2,"title":"new","completed":false}"""),
            headers = Headers.jsonContent(),
            statusCode = 201,
        ),
    )

    // An arbitrary response body with its metadata.
    put(
        path = "/todos/2",
        response = JsonBody("""{"completed":true}"""),
        responseHeaders = Headers.jsonContent(),
        statusCode = 202,
    )

    // A response produced lazily from the request registration code.
    patch("/todos/2") {
        HttpResponse(body = JsonBody("""{"completed":false}"""))
    }

    // Without selected http method
    http(
        method = HttpMethod.DELETE,
        path = "/todos/2",
        response = HttpResponse(statusCode = 204),
    )
}
```

The HTTP DSL supports `get`, `post`, `put`, `patch`, `head`, `delete`, and generic `http` mocks.
Each registered mock stores a matcher and a response:

- URLs are structured as `HttpUrl` components: scheme, host, effective port, encoded path and
  order-independent query parameters.
- Configured scheme and host use case-insensitive equality, port uses equality, and path keeps
  substring matching. Unspecified components are ignored.
- Configured query parameter names and their value sets must match; additional request query names
  are allowed.
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
import android.content.res.AssetManager
import dag.khinkal.molapi.http.assets.AssetJsonBody
import dag.khinkal.molapi.http.dsl.post
import dag.khinkal.molapi.http.model.Headers
import dag.khinkal.molapi.http.model.HttpUrl
import dag.khinkal.molapi.http.model.JsonBody
import dag.khinkal.molapi.http.registry.HttpInMemoryApiMockRegistry

fun createRegistry(
    context: Context,
    assetManager: AssetManager,
): HttpInMemoryApiMockRegistry =
    HttpInMemoryApiMockRegistry().apply {
        post(
            url = HttpUrl(path = "/todos"),
            headers = Headers.jsonContent(),
            body = AssetJsonBody(context, "requests/create-todo.json"),
            responseHeaders = Headers.jsonContent(),
            statusCode = 201,
        ) {
            JsonBody.fromAssets(context, "responses/todo-created.json")
        }
        post(
            url = HttpUrl(path = "/todos"),
            headers = Headers.jsonContent(),
            body = AssetJsonBody(assetManager, "requests/create-todo.json"),
            responseHeaders = Headers.jsonContent(),
            statusCode = 201,
        ) {
            JsonBody.fromAssets(assetManager, "responses/todo-created.json")
        }
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
entries from the registry. Request and response JSON bodies can be typed inline or loaded through
the platform document picker.

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

## License

    Copyright 2026 MolApi Contributors

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
