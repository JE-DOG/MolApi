package dag.khinkal.molapi.http.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
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
import dag.khinkal.molapi.http.editor.util.resultlauncher.model.FileContentType
import dag.khinkal.molapi.http.editor.util.resultlauncher.rememberFileResultLauncher
import dag.khinkal.molapi.http.model.HttpMethod
import dag.khinkal.molapi.http.registry.HttpApiMockRegistry
import molapi.molapi_http_editor.generated.resources.Res
import molapi.molapi_http_editor.generated.resources.add_mock_button
import molapi.molapi_http_editor.generated.resources.any_method_button
import molapi.molapi_http_editor.generated.resources.back_button
import molapi.molapi_http_editor.generated.resources.clear_button
import molapi.molapi_http_editor.generated.resources.collapse_content_button
import molapi.molapi_http_editor.generated.resources.create_mock_title
import molapi.molapi_http_editor.generated.resources.delete_button
import molapi.molapi_http_editor.generated.resources.expand_content_button
import molapi.molapi_http_editor.generated.resources.find_mocks_label
import molapi.molapi_http_editor.generated.resources.header_line_format_error
import molapi.molapi_http_editor.generated.resources.http_method_label
import molapi.molapi_http_editor.generated.resources.http_mocks_title
import molapi.molapi_http_editor.generated.resources.json_document_read_error
import molapi.molapi_http_editor.generated.resources.matcher_body_label
import molapi.molapi_http_editor.generated.resources.matcher_headers_label
import molapi.molapi_http_editor.generated.resources.matcher_url_label
import molapi.molapi_http_editor.generated.resources.mock_response_title
import molapi.molapi_http_editor.generated.resources.request_body_title
import molapi.molapi_http_editor.generated.resources.request_headers_title
import molapi.molapi_http_editor.generated.resources.request_matchers_title
import molapi.molapi_http_editor.generated.resources.response_body_label
import molapi.molapi_http_editor.generated.resources.response_body_title
import molapi.molapi_http_editor.generated.resources.response_headers_label
import molapi.molapi_http_editor.generated.resources.response_headers_title
import molapi.molapi_http_editor.generated.resources.response_status_code_label
import molapi.molapi_http_editor.generated.resources.select_json_document_button
import molapi.molapi_http_editor.generated.resources.status_code_label
import molapi.molapi_http_editor.generated.resources.status_code_number_error
import molapi.molapi_http_editor.generated.resources.visible_mocks_count
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

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
            contentPadding = PaddingValues(16.dp),
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
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(
                text = stringResource(Res.string.http_mocks_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = stringResource(Res.string.visible_mocks_count, mocksCount),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(onClick = onCreateMockClick) {
                Text(stringResource(Res.string.add_mock_button))
            }
            OutlinedButton(onClick = onClearClick) {
                Text(stringResource(Res.string.clear_button))
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
        label = { Text(stringResource(Res.string.find_mocks_label)) },
    )
}

@Composable
private fun CreateMockScreen(
    state: HttpMockEditorState,
    onBackClick: () -> Unit,
    onMockCreated: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val requestFileLauncher = rememberFileResultLauncher(
        onDocumentSelected = state::setDraftMatcherBodyFromDocument,
        onDocumentReadFailed = state::setJsonDocumentReadFailed,
    )
    val responseFileLauncher = rememberFileResultLauncher(
        onDocumentSelected = state::setDraftResponseBodyFromDocument,
        onDocumentReadFailed = state::setJsonDocumentReadFailed,
    )

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(Res.string.create_mock_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                TextButton(onClick = onBackClick) {
                    Text(stringResource(Res.string.back_button))
                }
            }

            DraftMatcherFields(
                state = state,
                onSelectJsonDocumentClick = {
                    requestFileLauncher.launch(listOf(FileContentType.JSON))
                },
            )
            HorizontalDivider()
            DraftResponseFields(
                state = state,
                onSelectJsonDocumentClick = {
                    responseFileLauncher.launch(listOf(FileContentType.JSON))
                },
            )
            state.draftError?.let { error ->
                Text(
                    text = error.asText(),
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
                Text(stringResource(Res.string.add_mock_button))
            }
        }
    }
}

@Composable
private fun DraftMatcherFields(
    state: HttpMockEditorState,
    onSelectJsonDocumentClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SectionTitle(stringResource(Res.string.request_matchers_title))
        OutlinedTextField(
            value = state.draftMatcherUrl,
            onValueChange = { value -> state.draftMatcherUrl = value },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text(stringResource(Res.string.matcher_url_label)) },
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
            label = { Text(stringResource(Res.string.matcher_headers_label)) },
        )
        OutlinedTextField(
            value = state.draftMatcherBody,
            onValueChange = { value -> state.draftMatcherBody = value },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            label = { Text(stringResource(Res.string.matcher_body_label)) },
        )
        OutlinedButton(onClick = onSelectJsonDocumentClick) {
            Text(stringResource(Res.string.select_json_document_button))
        }
    }
}

@Composable
private fun DraftResponseFields(
    state: HttpMockEditorState,
    onSelectJsonDocumentClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SectionTitle(stringResource(Res.string.mock_response_title))
        OutlinedTextField(
            value = state.draftResponseStatusCode,
            onValueChange = { value -> state.draftResponseStatusCode = value },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text(stringResource(Res.string.response_status_code_label)) },
        )
        OutlinedTextField(
            value = state.draftResponseHeaders,
            onValueChange = { value -> state.draftResponseHeaders = value },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            label = { Text(stringResource(Res.string.response_headers_label)) },
        )
        OutlinedTextField(
            value = state.draftResponseBody,
            onValueChange = { value -> state.draftResponseBody = value },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            label = { Text(stringResource(Res.string.response_body_label)) },
        )
        OutlinedButton(onClick = onSelectJsonDocumentClick) {
            Text(stringResource(Res.string.select_json_document_button))
        }
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
            text = stringResource(Res.string.http_method_label),
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
                    Text(stringResource(Res.string.any_method_button))
                }
            } else {
                OutlinedButton(onClick = { onMethodSelected(null) }) {
                    Text(stringResource(Res.string.any_method_button))
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

    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = listOfNotNull(
                            mock.method?.name ?: stringResource(Res.string.any_method_button),
                            mock.urlText,
                        )
                            .joinToString(" "),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Text(
                        text = stringResource(Res.string.status_code_label, mock.statusCode),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                TextButton(onClick = onDeleteClick) {
                    Text(stringResource(Res.string.delete_button))
                }
            }

            if (contentSections.isNotEmpty()) {
                if (isContentExpanded) {
                    SelectionContainer {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            contentSections.forEach { section ->
                                MockContentSection(section)
                            }
                        }
                    }
                }

                TextButton(
                    onClick = { isContentExpanded = !isContentExpanded },
                ) {
                    Text(
                        stringResource(
                            if (isContentExpanded) {
                                Res.string.collapse_content_button
                            } else {
                                Res.string.expand_content_button
                            },
                        ),
                    )
                }
            }
        }
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
            text = stringResource(section.title),
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
    val title: StringResource,
    val content: String,
)

@Composable
private fun HttpMockEditorDraftError.asText(): String = when (this) {
    HttpMockEditorDraftError.StatusCodeMustBeNumber -> stringResource(
        Res.string.status_code_number_error,
    )

    is HttpMockEditorDraftError.InvalidHeaderLine -> stringResource(
        Res.string.header_line_format_error,
        lineNumber,
    )

    HttpMockEditorDraftError.JsonDocumentReadFailed -> stringResource(
        Res.string.json_document_read_error,
    )
}

private fun HttpMockEditorMock.contentSections(): List<MockContentSection> = buildList {
    if (!requestHeaders.isNullOrBlank()) {
        add(
            MockContentSection(
                title = Res.string.request_headers_title,
                content = requestHeaders,
            ),
        )
    }
    if (!requestBody.isNullOrBlank()) {
        add(
            MockContentSection(
                title = Res.string.request_body_title,
                content = requestBody,
            ),
        )
    }
    if (!responseHeaders.isNullOrBlank()) {
        add(
            MockContentSection(
                title = Res.string.response_headers_title,
                content = responseHeaders,
            ),
        )
    }
    if (!responseBody.isNullOrBlank()) {
        add(
            MockContentSection(
                title = Res.string.response_body_title,
                content = responseBody,
            ),
        )
    }
}
