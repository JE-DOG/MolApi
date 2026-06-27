package dag.khinkal.molapi

import dag.khinkal.molapi.core.matcher.ApiRequestMatcher
import dag.khinkal.molapi.core.model.ApiMock
import dag.khinkal.molapi.core.registry.ApiMockRegistry
import dag.khinkal.molapi.http.dsl.get
import dag.khinkal.molapi.http.model.HttpRequest
import dag.khinkal.molapi.http.model.HttpResponse
import dag.khinkal.molapi.http.registry.HttpApiMockRegistry
import dag.khinkal.molapi.http.registry.HttpInMemoryApiMockRegistry
import dag.khinkal.molapi.http.serializable.util.JsonHttpResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class TodoApiTest {

    @Test
    fun fetchTodoUsesMolApiMockAndDecodesResponse() = runTest {
        val registry: HttpApiMockRegistry = HttpInMemoryApiMockRegistry().apply {
            get("/todos") {
                JsonHttpResponse(
                    serializableBody = listOf(
                        Todo(
                            userId = 99,
                            id = 42,
                            title = "todo from molapi",
                            completed = true,
                        ),
                    ),
                )
            }
        }
        val client = createTodoHttpClient(
            molApiConfig = MolApiClientConfig(
                isEnabled = true,
                registry = registry,
            ),
        )
        val api = TodoApi(client)

        try {
            val todo = api.fetchTodo()

            assertEquals(
                listOf(
                    Todo(
                        userId = 99,
                        id = 42,
                        title = "todo from molapi",
                        completed = true,
                    )
                ),
                todo,
            )
        } finally {
            client.close()
        }
    }

    @Test
    fun molApiClientConfigAcceptsAnyHttpApiMockRegistryImplementation() {
        val registry = FakeHttpApiMockRegistry()

        val config = MolApiClientConfig(
            isEnabled = true,
            registry = registry,
        )

        assertSame(registry, config.registry)
    }

    private class FakeHttpApiMockRegistry : ApiMockRegistry<
            HttpRequest,
            ApiRequestMatcher<HttpRequest>,
            HttpResponse,
            > {

        override val mocks: StateFlow<List<ApiMock<HttpRequest, ApiRequestMatcher<HttpRequest>, HttpResponse>>> =
            MutableStateFlow(emptyList())

        override fun add(
            mock: ApiMock<HttpRequest, ApiRequestMatcher<HttpRequest>, HttpResponse>,
        ) = Unit

        override fun remove(mock: ApiMock<HttpRequest, ApiRequestMatcher<HttpRequest>, HttpResponse>): Boolean {
            return true
        }

        override fun clear() = Unit

        override fun find(request: HttpRequest): ApiMock<
                HttpRequest,
                ApiRequestMatcher<HttpRequest>,
                HttpResponse,
                >? = null
    }
}
