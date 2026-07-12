package dag.khinkal.molapi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dag.khinkal.molapi.http.editor.HttpMockEditorScreen
import dag.khinkal.molapi.http.registry.HttpApiMockRegistry
import kotlinx.coroutines.launch

@Composable
public fun App(
    molApiConfig: MolApiClientConfig = MolApiClientConfig(),
) {
    val coroutineScope = rememberCoroutineScope()

    MaterialTheme {
        val client = remember(molApiConfig) { createTodoHttpClient(molApiConfig) }
        val api = remember(client) { TodoApi(client) }
        var uiState by remember { mutableStateOf<TodoUiState>(TodoUiState.Loading) }
        var isEditorVisible by remember { mutableStateOf(false) }

        LaunchedEffect(api) {
            uiState = try {
                TodoUiState.Content(api.fetchTodo())
            } catch (error: Throwable) {
                TodoUiState.Error(error.message ?: "Failed to load todos")
            }
        }
        DisposableEffect(client) {
            onDispose {
                client.close()
            }
        }

        TodoScreen(
            uiState = uiState,
            isEditorEnabled = molApiConfig.isEnabled,
            onOpenEditorClick = { isEditorVisible = true },
            onRefresh = {
                coroutineScope.launch {
                    uiState = try {
                        TodoUiState.Content(api.fetchTodo())
                    } catch (error: Throwable) {
                        TodoUiState.Error(error.message ?: "Failed to load todos")
                    }
                }
            },
        )
        if (molApiConfig.isEnabled && isEditorVisible) {
            HttpMockEditorDialog(
                registry = molApiConfig.registry,
                onDismissRequest = { isEditorVisible = false },
            )
        }
    }
}

private sealed interface TodoUiState {
    data object Loading : TodoUiState
    data class Content(val todos: List<Todo>) : TodoUiState
    data class Error(val message: String) : TodoUiState
}

@Composable
private fun TodoScreen(
    uiState: TodoUiState,
    isEditorEnabled: Boolean,
    onOpenEditorClick: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .safeContentPadding()
            .fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Todos",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                Button(onClick = onRefresh) {
                    Text("Refresh")
                }
                if (isEditorEnabled) {
                    Button(onClick = onOpenEditorClick) {
                        Text("Open editor")
                    }
                }
            }

            when (uiState) {
                TodoUiState.Loading -> CircularProgressIndicator()
                is TodoUiState.Error -> Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                )

                is TodoUiState.Content -> TodoList(uiState.todos)
            }
        }
    }
}

@Composable
private fun HttpMockEditorDialog(
    registry: HttpApiMockRegistry,
    onDismissRequest: () -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Mock editor",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    TextButton(onClick = onDismissRequest) {
                        Text("Close")
                    }
                }
                HttpMockEditorScreen(
                    registry = registry,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun TodoList(
    todos: List<Todo>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(todos, key = { it.id }) { todo ->
            TodoRow(todo)
        }
    }
}

@Composable
private fun TodoRow(todo: Todo) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "#${todo.id}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = if (todo.completed) "Completed" else "Not completed",
                style = MaterialTheme.typography.labelLarge,
            )
        }
        Text(
            text = todo.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "User ${todo.userId}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview
@Composable
private fun TodoScreenPreview() {
    MaterialTheme {
        TodoScreen(
            TodoUiState.Content(
                listOf(
                    Todo(
                        userId = 1,
                        id = 1,
                        title = "delectus aut autem",
                        completed = false,
                    ),
                ),
            ),
            isEditorEnabled = true,
            onOpenEditorClick = {},
            onRefresh = {},
        )
    }
}
