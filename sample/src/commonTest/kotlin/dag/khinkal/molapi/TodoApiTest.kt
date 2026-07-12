package dag.khinkal.molapi

import dag.khinkal.molapi.http.dsl.get
import dag.khinkal.molapi.http.dsl.post
import dag.khinkal.molapi.http.model.Headers
import dag.khinkal.molapi.http.model.HttpResponse
import dag.khinkal.molapi.http.model.JsonBody
import dag.khinkal.molapi.http.registry.HttpApiMockRegistry
import dag.khinkal.molapi.http.registry.HttpInMemoryApiMockRegistry
import dag.khinkal.molapi.http.serializable.util.JsonHttpResponse
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
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

    @Test
    fun mockCanBeRegisteredWithJsonShortcut() = runTest {
        val registry: HttpApiMockRegistry = HttpInMemoryApiMockRegistry().apply {
            get(
                path = "/todos",
                json = """
                    [
                      {
                        "userId": 7,
                        "id": 11,
                        "title": "todo from json shortcut",
                        "completed": false
                      }
                    ]
                """.trimIndent(),
            )
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
                        userId = 7,
                        id = 11,
                        title = "todo from json shortcut",
                        completed = false,
                    ),
                ),
                todo,
            )
        } finally {
            client.close()
        }
    }

    @Test
    fun mockCanReturnCustomStatusAndHeaders() = runTest {
        val registry: HttpApiMockRegistry = HttpInMemoryApiMockRegistry().apply {
            get("/todos") {
                HttpResponse(
                    headers = Headers(
                        mapOf(
                            "X-MolApi-Scenario" to setOf("empty-list"),
                        ),
                    ),
                    body = JsonBody("[]"),
                    statusCode = 202,
                )
            }
        }
        val client = createTodoHttpClient(
            molApiConfig = MolApiClientConfig(
                isEnabled = true,
                registry = registry,
            ),
        )

        try {
            val response = client.get("https://jsonplaceholder.typicode.com/todos")

            assertEquals(202, response.status.value)
            assertEquals("empty-list", response.headers["X-MolApi-Scenario"])
            assertEquals("[]", response.bodyAsText())
        } finally {
            client.close()
        }
    }

    @Test
    fun mockCanMatchRequestBody() = runTest {
        val registry: HttpApiMockRegistry = HttpInMemoryApiMockRegistry().apply {
            post(
                path = "/todos",
                body = JsonBody("""{"title":"new todo"}"""),
            ) {
                JsonHttpResponse(
                    serializableBody = Todo(
                        userId = 1,
                        id = 101,
                        title = "new todo",
                        completed = false,
                    ),
                    statusCode = 201,
                )
            }
        }
        val client = createTodoHttpClient(
            molApiConfig = MolApiClientConfig(
                isEnabled = true,
                registry = registry,
            ),
        )

        try {
            val response = client.post("https://jsonplaceholder.typicode.com/todos") {
                setBody("""{"title":"new todo"}""")
            }

            assertEquals(201, response.status.value)
            assertEquals(
                """
                    {
                        "userId": 1,
                        "id": 101,
                        "title": "new todo",
                        "completed": false
                    }
                """.trimIndent().filterNot(Char::isWhitespace),
                response.bodyAsText().filterNot(Char::isWhitespace),
            )
        } finally {
            client.close()
        }
    }

}
