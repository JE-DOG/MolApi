package dag.khinkal.molapi

import dag.khinkal.molapi.http.editor.MolApiEditor
import dag.khinkal.molapi.http.ktor.plugin.MolApiKtorPlugin
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

internal val AppJson = Json {
    ignoreUnknownKeys = true
}

@Serializable
public data class Todo(
    public val userId: Int,
    public val id: Int,
    public val title: String,
    public val completed: Boolean,
)

public class TodoApi(
    private val client: HttpClient = createTodoHttpClient(),
) {
    public suspend fun fetchTodo(): List<Todo> =
        client.get(TODO_URL).body()

    public companion object {
        private const val TODO_URL = "https://jsonplaceholder.typicode.com/todos"
    }
}

public fun createTodoHttpClient(): HttpClient =
    HttpClient {
        install(MolApiKtorPlugin) {
            registry = MolApiEditor.registry
        }
        install(ContentNegotiation) {
            json(AppJson)
        }
    }
