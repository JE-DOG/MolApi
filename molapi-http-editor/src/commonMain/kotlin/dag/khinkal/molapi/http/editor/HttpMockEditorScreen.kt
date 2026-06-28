package dag.khinkal.molapi.http.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import dag.khinkal.molapi.http.model.HttpMethod
import dag.khinkal.molapi.http.registry.HttpApiMockRegistry

private sealed interface HttpMockEditorRoute : NavKey

private data object MockListRoute : HttpMockEditorRoute

private data object CreateMockRoute : HttpMockEditorRoute

@Composable
public fun HttpMockEditorScreen(
    registry: HttpApiMockRegistry,
    modifier: Modifier = Modifier,
) {
    val state = remember(registry) { HttpMockEditorState(registry) }

    HttpMockEditorScreen(
        state = state,
        modifier = modifier,
    )
}

@Composable
internal fun HttpMockEditorScreen(
    state: HttpMockEditorState,
    modifier: Modifier = Modifier,
) {
    val backStack = remember { NavBackStack<HttpMockEditorRoute>(MockListRoute) }

    NavigationContent(
        backStack = backStack,
        modifier = modifier,
        state = state,
    )
}

@Composable
private fun NavigationContent(
    backStack: NavBackStack<HttpMockEditorRoute>,
    state: HttpMockEditorState,
    modifier: Modifier = Modifier,
) {
    when (backStack.lastOrNull() ?: MockListRoute) {
        MockListRoute -> HttpMockListScreen(
            state = state,
            modifier = modifier,
            onCreateMockClick = { backStack.add(CreateMockRoute) },
        )

        CreateMockRoute -> CreateMockScreen(
            state = state,
            modifier = modifier,
            onBackClick = {
                if (backStack.size > 1) {
                    backStack.removeLastOrNull()
                }
            },
            onMockCreated = {
                if (backStack.size > 1) {
                    backStack.removeLastOrNull()
                }
            },
        )
    }
}

@Composable
private fun HttpMockListScreen(
    state: HttpMockEditorState,
    onCreateMockClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val visibleMocks = state.collectVisibleMocks()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            item("header") {
                Header(
                    mocksCount = visibleMocks.size,
                    onCreateMockClick = onCreateMockClick,
                    onClearClick = state::clearMocks,
                )
            }

            item("search") {
                SearchField(state)
            }

            items(
                items = visibleMocks,
                key = { mock -> mock.id },
            ) { mock ->
                MockRow(
                    mock = mock,
                    onDeleteClick = { state.removeMock(mock) },
                )
            }
        }
    }
}

@Composable
private fun Header(
    mocksCount: Int,
    onCreateMockClick: () -> Unit,
    onClearClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(
                text = "HTTP mocks",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "$mocksCount visible",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(onClick = onCreateMockClick) {
                Text("Add mock")
            }
            OutlinedButton(onClick = onClearClick) {
                Text("Clear")
            }
        }
    }
}

@Composable
private fun SearchField(state: HttpMockEditorState) {
    OutlinedTextField(
        value = state.searchQuery,
        onValueChange = { value -> state.searchQuery = value },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        label = { Text("Find mocks") },
    )
}

@Composable
private fun CreateMockScreen(
    state: HttpMockEditorState,
    onBackClick: () -> Unit,
    onMockCreated: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Create mock",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                TextButton(onClick = onBackClick) {
                    Text("Back")
                }
            }

            DraftMatcherFields(state)
            HorizontalDivider()
            DraftResponseFields(state)
            state.draftError?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Button(
                onClick = {
                    if (state.addDraftMock()) {
                        onMockCreated()
                    }
                },
            ) {
                Text("Add mock")
            }
        }
    }
}

@Composable
private fun DraftMatcherFields(state: HttpMockEditorState) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SectionTitle("Request matchers")
        OutlinedTextField(
            value = state.draftMatcherUrl,
            onValueChange = { value -> state.draftMatcherUrl = value },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Matcher URL") },
        )
        MethodPicker(
            selectedMethod = state.draftMatcherMethod,
            onMethodSelected = { method -> state.draftMatcherMethod = method },
        )
        OutlinedTextField(
            value = state.draftMatcherHeaders,
            onValueChange = { value -> state.draftMatcherHeaders = value },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            label = { Text("Matcher headers") },
        )
        OutlinedTextField(
            value = state.draftMatcherBody,
            onValueChange = { value -> state.draftMatcherBody = value },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            label = { Text("Matcher JSON body") },
        )
    }
}

@Composable
private fun DraftResponseFields(state: HttpMockEditorState) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SectionTitle("Mock response")
        OutlinedTextField(
            value = state.draftResponseStatusCode,
            onValueChange = { value -> state.draftResponseStatusCode = value },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Response status code") },
        )
        OutlinedTextField(
            value = state.draftResponseHeaders,
            onValueChange = { value -> state.draftResponseHeaders = value },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            label = { Text("Response headers") },
        )
        OutlinedTextField(
            value = state.draftResponseBody,
            onValueChange = { value -> state.draftResponseBody = value },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            label = { Text("Response JSON body") },
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
private fun MethodPicker(
    selectedMethod: HttpMethod?,
    onMethodSelected: (HttpMethod?) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "HTTP method",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (selectedMethod == null) {
                Button(onClick = { onMethodSelected(null) }) {
                    Text("ANY")
                }
            } else {
                OutlinedButton(onClick = { onMethodSelected(null) }) {
                    Text("ANY")
                }
            }
            HttpMethod.entries.forEach { method ->
                if (method == selectedMethod) {
                    Button(onClick = { onMethodSelected(method) }) {
                        Text(method.name)
                    }
                } else {
                    OutlinedButton(onClick = { onMethodSelected(method) }) {
                        Text(method.name)
                    }
                }
            }
        }
    }
}

@Composable
private fun MockRow(
    mock: HttpMockEditorMock,
    onDeleteClick: () -> Unit,
) {
    var isContentExpanded by remember(mock.id) { mutableStateOf(false) }
    val contentSections = mock.contentSections()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = listOfNotNull(mock.method?.name ?: "ANY", mock.url)
                        .joinToString(" "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            TextButton(onClick = onDeleteClick) {
                Text("Delete")
            }
        }
        Text(
            text = "Status ${mock.statusCode}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
        )

        if (contentSections.isNotEmpty()) {
            TextButton(
                onClick = { isContentExpanded = !isContentExpanded },
            ) {
                Text(if (isContentExpanded) "Collapse content" else "Expand content")
            }
            SelectionContainer {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (isContentExpanded) {
                                Modifier
                            } else {
                                Modifier.heightIn(max = 100.dp)
                            }
                        ),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    contentSections.forEach { section ->
                        MockContentSection(section)
                    }
                }
            }
        }
        HorizontalDivider()
    }
}

@Composable
private fun MockContentSection(
    section: MockContentSection,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = section.title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = section.content,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private data class MockContentSection(
    val title: String,
    val content: String,
)

private fun HttpMockEditorMock.contentSections(): List<MockContentSection> = buildList {
    if (!requestHeaders.isNullOrBlank()) {
        add(
            MockContentSection(
                title = "Request headers",
                content = requestHeaders,
            ),
        )
    }
    if (!requestBody.isNullOrBlank()) {
        add(
            MockContentSection(
                title = "Request body",
                content = requestBody,
            ),
        )
    }
    if (!responseHeaders.isNullOrBlank()) {
        add(
            MockContentSection(
                title = "Response headers",
                content = responseHeaders,
            ),
        )
    }
    if (!responseBody.isNullOrBlank()) {
        add(
            MockContentSection(
                title = "Response body",
                content = responseBody,
            ),
        )
    }
}
