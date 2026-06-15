package dag.khinkal.molapi.http.ktor

import dag.khinkal.molapi.http.ktor.plugin.MolApiKtorPlugin
import dag.khinkal.molapi.http.matcher.BaseHttpRequestMatcher
import dag.khinkal.molapi.http.model.BaseHttpApiMock
import dag.khinkal.molapi.http.model.Headers
import dag.khinkal.molapi.http.model.HttpMethod
import dag.khinkal.molapi.http.model.HttpResponse
import dag.khinkal.molapi.http.model.JsonBody
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MolApiKtorPluginTest {

    @Test
    fun returnsMockResponseWhenRegistryFindsMatch() = runTest {
        var realRequestCount = 0
        val registry = dag.khinkal.molapi.http.registry.HttpApiMockRegistry().apply {
            add(
                BaseHttpApiMock(
                    url = "tasks_mock",
                    matcher = BaseHttpRequestMatcher(
                        urlRegex = "https://some.com/tasks",
                        method = HttpMethod.GET
                    ),
                    response = HttpResponse(
                        headers = Headers.empty(),
                        body = JsonBody("""{"tasks":[]}"""),
                        statusCode = 201
                    )
                )
            )
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
    fun proceedsWithRealRequestWhenRegistryDoesNotFindMatch() = runTest {
        var realRequestCount = 0
        val client = HttpClient(
            MockEngine {
                realRequestCount += 1
                respond("real", status = HttpStatusCode.Accepted)
            }
        ) {
            install(MolApiKtorPlugin) {
                registry = dag.khinkal.molapi.http.registry.HttpApiMockRegistry()
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
        val registry = dag.khinkal.molapi.http.registry.HttpApiMockRegistry().apply {
            add(
                BaseHttpApiMock(
                    url = "create_task_mock",
                    matcher = BaseHttpRequestMatcher(
                        urlRegex = "https://some.com/tasks",
                        method = HttpMethod.POST,
                        body = JsonBody("{}")
                    ),
                    response = HttpResponse(
                        headers = Headers.empty(),
                        body = JsonBody("""{"created":true}""")
                    )
                )
            )
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
