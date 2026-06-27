package dag.khinkal.molapi

import dag.khinkal.molapi.http.dsl.get
import dag.khinkal.molapi.http.registry.HttpApiMockRegistry
import dag.khinkal.molapi.http.registry.HttpInMemoryApiMockRegistry
import dag.khinkal.molapi.http.serializable.util.JsonHttpResponse
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

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
}
