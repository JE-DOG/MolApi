package dag.khinkal.molapi.http.ktor

import dag.khinkal.molapi.http.dsl.get
import dag.khinkal.molapi.http.dsl.head
import dag.khinkal.molapi.http.dsl.post
import dag.khinkal.molapi.http.ktor.plugin.MolApiKtorPlugin
import dag.khinkal.molapi.http.model.Headers
import dag.khinkal.molapi.http.model.HttpResponse
import dag.khinkal.molapi.http.model.JsonBody
import dag.khinkal.molapi.http.registry.HttpInMemoryApiMockRegistry
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.get
import io.ktor.client.request.head
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MolApiKtorPluginTest {

    @Test
    fun returnsMockResponseWhenRegistryFindsMatch() = runTest {
        var realRequestCount = 0
        val registry = HttpInMemoryApiMockRegistry().apply {
            get(
                scheme = "https",
                host = "some.com",
                path = "/tasks",
            ) {
                HttpResponse(
                    headers = Headers.empty(),
                    body = JsonBody("""{"tasks":[]}"""),
                    statusCode = 201,
                )
            }
        }
        val client = HttpClient(
            MockEngine {
                realRequestCount += 1
                respond("real")
            }
        ) {
            install(MolApiKtorPlugin) {
                this.registry = registry
            }
        }

        val response = client.get("https://some.com/tasks")

        assertEquals(201, response.status.value)
        assertEquals("""{"tasks":[]}""", response.bodyAsText())
        assertEquals(0, realRequestCount)

        client.close()
    }

    @Test
    fun returnsMockResponseForHeadRequest() = runTest {
        var realRequestCount = 0
        val registry = HttpInMemoryApiMockRegistry().apply {
            head(
                scheme = "https",
                host = "some.com",
                path = "/tasks",
            ) {
                HttpResponse(statusCode = 204)
            }
        }
        val client = HttpClient(
            MockEngine {
                realRequestCount += 1
                respond("real")
            }
        ) {
            install(MolApiKtorPlugin) {
                this.registry = registry
            }
        }

        val response = client.head("https://some.com/tasks")

        assertEquals(204, response.status.value)
        assertEquals(0, realRequestCount)

        client.close()
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun mockedResponsePassesThroughReceivePipeline() = runTest {
        var observedStatusCode: Int? = null
        val registry = HttpInMemoryApiMockRegistry().apply {
            get(
                scheme = "https",
                host = "some.com",
                path = "/tasks",
            ) {
                HttpResponse(
                    body = JsonBody("""{"tasks":[]}"""),
                    statusCode = 203,
                )
            }
        }
        val client = HttpClient(
            MockEngine {
                respond("real")
            }
        ) {
            install(MolApiKtorPlugin) {
                this.registry = registry
            }
            install(ResponseObserver) {
                onResponse { response ->
                    observedStatusCode = response.status.value
                }
            }
        }

        val response = client.get("https://some.com/tasks")

        assertEquals("""{"tasks":[]}""", response.bodyAsText())
        advanceUntilIdle()
        assertEquals(203, observedStatusCode)

        client.close()
    }

    @Test
    fun proceedsWithRealRequestWhenRegistryDoesNotFindMatch() = runTest {
        var realRequestCount = 0
        val client = HttpClient(
            MockEngine {
                realRequestCount += 1
                respond("real", status = HttpStatusCode.Accepted)
            }
        ) {
            install(MolApiKtorPlugin) {
                registry = HttpInMemoryApiMockRegistry()
            }
        }

        val response = client.get("https://some.com/tasks")

        assertEquals(202, response.status.value)
        assertEquals("real", response.bodyAsText())
        assertEquals(1, realRequestCount)

        client.close()
    }

    @Test
    fun matchesRequestBodyWhenKtorRequestHasTextBody() = runTest {
        val registry = HttpInMemoryApiMockRegistry().apply {
            post(
                scheme = "https",
                host = "some.com",
                path = "/tasks",
                body = JsonBody("{}"),
            ) {
                HttpResponse(
                    headers = Headers.empty(),
                    body = JsonBody("""{"created":true}"""),
                )
            }
        }
        val client = HttpClient(
            MockEngine {
                respond("real")
            }
        ) {
            install(MolApiKtorPlugin) {
                this.registry = registry
            }
        }

        val response = client.post("https://some.com/tasks") {
            setBody("{}")
        }

        assertEquals(200, response.status.value)
        assertEquals("""{"created":true}""", response.bodyAsText())

        client.close()
    }
}
