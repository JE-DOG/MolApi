package dag.khinkal.molapi.http.editor.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dag.khinkal.molapi.http.editor.HttpMockEditorDraft
import dag.khinkal.molapi.http.editor.HttpMockEditorDraftError
import dag.khinkal.molapi.http.editor.HttpMockEditorViewModel
import dag.khinkal.molapi.http.editor.util.resultlauncher.model.FileContentType
import dag.khinkal.molapi.http.editor.util.resultlauncher.rememberFileResultLauncher
import dag.khinkal.molapi.http.model.HttpMethod
import molapi.molapi_http_editor.generated.resources.Res
import molapi.molapi_http_editor.generated.resources.any_method_button
import molapi.molapi_http_editor.generated.resources.back_button
import molapi.molapi_http_editor.generated.resources.catch_all_description
import molapi.molapi_http_editor.generated.resources.catch_all_title
import molapi.molapi_http_editor.generated.resources.create_mock_button
import molapi.molapi_http_editor.generated.resources.create_mock_subtitle
import molapi.molapi_http_editor.generated.resources.create_mock_title
import molapi.molapi_http_editor.generated.resources.header_line_format_error
import molapi.molapi_http_editor.generated.resources.headers_supporting_text
import molapi.molapi_http_editor.generated.resources.http_method_label
import molapi.molapi_http_editor.generated.resources.ic_add
import molapi.molapi_http_editor.generated.resources.ic_arrow_back
import molapi.molapi_http_editor.generated.resources.ic_error
import molapi.molapi_http_editor.generated.resources.ic_rule
import molapi.molapi_http_editor.generated.resources.ic_upload_file
import molapi.molapi_http_editor.generated.resources.json_document_read_error
import molapi.molapi_http_editor.generated.resources.matcher_body_label
import molapi.molapi_http_editor.generated.resources.matcher_body_supporting_text
import molapi.molapi_http_editor.generated.resources.matcher_headers_label
import molapi.molapi_http_editor.generated.resources.matcher_url_label
import molapi.molapi_http_editor.generated.resources.matcher_url_supporting_text
import molapi.molapi_http_editor.generated.resources.mock_response_description
import molapi.molapi_http_editor.generated.resources.mock_response_title
import molapi.molapi_http_editor.generated.resources.request_matchers_description
import molapi.molapi_http_editor.generated.resources.request_matchers_title
import molapi.molapi_http_editor.generated.resources.response_body_label
import molapi.molapi_http_editor.generated.resources.response_body_supporting_text
import molapi.molapi_http_editor.generated.resources.response_headers_label
import molapi.molapi_http_editor.generated.resources.response_headers_supporting_text
import molapi.molapi_http_editor.generated.resources.response_status_code_label
import molapi.molapi_http_editor.generated.resources.response_status_code_supporting_text
import molapi.molapi_http_editor.generated.resources.section_then_label
import molapi.molapi_http_editor.generated.resources.section_when_label
import molapi.molapi_http_editor.generated.resources.select_json_document_button
import molapi.molapi_http_editor.generated.resources.status_code_number_error
import molapi.molapi_http_editor.generated.resources.status_code_range_error
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun CreateHttpMockScreen(
    viewModel: HttpMockEditorViewModel,
    modifier: Modifier = Modifier,
) {
    val matcherFileLauncher = rememberFileResultLauncher(
        onDocumentSelected = viewModel::onDraftMatcherBodyDocumentSelected,
        onDocumentReadFailed = viewModel::onJsonDocumentReadFailed,
    )
    val responseFileLauncher = rememberFileResultLauncher(
        onDocumentSelected = viewModel::onDraftResponseBodyDocumentSelected,
        onDocumentReadFailed = viewModel::onJsonDocumentReadFailed,
    )
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val draft = viewModel.draft
    val onCreateClick = {
        focusManager.clearFocus()
        if (viewModel.addDraftMock()) {
            viewModel.showMockList()
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResource(Res.string.create_mock_title))
                        Text(
                            text = stringResource(Res.string.create_mock_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = viewModel::showMockList) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_arrow_back),
                            contentDescription = stringResource(Res.string.back_button),
                        )
                    }
                },
            )
        },
        bottomBar = {
            CreateMockActionBar(onCreateClick = onCreateClick)
        },
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 1120.dp)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                if (draft.error == HttpMockEditorDraftError.JsonDocumentReadFailed) {
                    ErrorBanner(stringResource(Res.string.json_document_read_error))
                }

                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    if (maxWidth >= 840.dp) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.Top,
                        ) {
                            RequestMatcherCard(
                                draft = draft,
                                viewModel = viewModel,
                                onSelectJsonDocumentClick = {
                                    matcherFileLauncher.launch(listOf(FileContentType.JSON))
                                },
                                modifier = Modifier.weight(1f),
                            )
                            MockResponseCard(
                                draft = draft,
                                viewModel = viewModel,
                                onSelectJsonDocumentClick = {
                                    responseFileLauncher.launch(listOf(FileContentType.JSON))
                                },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            RequestMatcherCard(
                                draft = draft,
                                viewModel = viewModel,
                                onSelectJsonDocumentClick = {
                                    matcherFileLauncher.launch(listOf(FileContentType.JSON))
                                },
                            )
                            MockResponseCard(
                                draft = draft,
                                viewModel = viewModel,
                                onSelectJsonDocumentClick = {
                                    responseFileLauncher.launch(listOf(FileContentType.JSON))
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RequestMatcherCard(
    draft: HttpMockEditorDraft,
    viewModel: HttpMockEditorViewModel,
    onSelectJsonDocumentClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val headerError = draft.error as? HttpMockEditorDraftError.InvalidMatcherHeaderLine

    EditorSectionCard(
        eyebrow = stringResource(Res.string.section_when_label),
        title = stringResource(Res.string.request_matchers_title),
        description = stringResource(Res.string.request_matchers_description),
        modifier = modifier,
    ) {
        HttpMethodPicker(
            selectedMethod = draft.matcherMethod,
            onMethodSelected = viewModel::onDraftMatcherMethodChanged,
        )
        OutlinedTextField(
            value = draft.matcherUrl,
            onValueChange = viewModel::onDraftMatcherUrlChanged,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = EditorFieldShape,
            label = { Text(stringResource(Res.string.matcher_url_label)) },
            supportingText = {
                Text(stringResource(Res.string.matcher_url_supporting_text))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Next,
            ),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = FontFamily.Monospace,
            ),
        )
        if (draft.matcherMethod == null && draft.matcherUrl.isBlank()) {
            CatchAllNotice()
        }
        OutlinedTextField(
            value = draft.matcherHeaders,
            onValueChange = viewModel::onDraftMatcherHeadersChanged,
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 7,
            shape = EditorFieldShape,
            label = { Text(stringResource(Res.string.matcher_headers_label)) },
            isError = headerError != null,
            supportingText = {
                Text(
                    if (headerError == null) {
                        stringResource(Res.string.headers_supporting_text)
                    } else {
                        stringResource(
                            Res.string.header_line_format_error,
                            headerError.lineNumber,
                        )
                    },
                )
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace,
            ),
        )
        JsonBodyInput(
            value = draft.matcherBody,
            onValueChange = viewModel::onDraftMatcherBodyChanged,
            label = stringResource(Res.string.matcher_body_label),
            supportingText = stringResource(Res.string.matcher_body_supporting_text),
            onSelectJsonDocumentClick = onSelectJsonDocumentClick,
        )
    }
}

@Composable
private fun MockResponseCard(
    draft: HttpMockEditorDraft,
    viewModel: HttpMockEditorViewModel,
    onSelectJsonDocumentClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val statusError = when (draft.error) {
        HttpMockEditorDraftError.StatusCodeMustBeNumber -> stringResource(
            Res.string.status_code_number_error,
        )

        HttpMockEditorDraftError.StatusCodeOutOfRange -> stringResource(
            Res.string.status_code_range_error,
        )

        else -> null
    }
    val headerError = draft.error as? HttpMockEditorDraftError.InvalidResponseHeaderLine

    EditorSectionCard(
        eyebrow = stringResource(Res.string.section_then_label),
        title = stringResource(Res.string.mock_response_title),
        description = stringResource(Res.string.mock_response_description),
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = draft.responseStatusCode,
            onValueChange = viewModel::onDraftResponseStatusCodeChanged,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = EditorFieldShape,
            label = { Text(stringResource(Res.string.response_status_code_label)) },
            isError = statusError != null,
            supportingText = {
                Text(
                    statusError
                        ?: stringResource(Res.string.response_status_code_supporting_text),
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next,
            ),
        )
        OutlinedTextField(
            value = draft.responseHeaders,
            onValueChange = viewModel::onDraftResponseHeadersChanged,
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 7,
            shape = EditorFieldShape,
            label = { Text(stringResource(Res.string.response_headers_label)) },
            isError = headerError != null,
            supportingText = {
                Text(
                    if (headerError == null) {
                        stringResource(Res.string.response_headers_supporting_text)
                    } else {
                        stringResource(
                            Res.string.header_line_format_error,
                            headerError.lineNumber,
                        )
                    },
                )
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace,
            ),
        )
        JsonBodyInput(
            value = draft.responseBody,
            onValueChange = viewModel::onDraftResponseBodyChanged,
            label = stringResource(Res.string.response_body_label),
            supportingText = stringResource(Res.string.response_body_supporting_text),
            onSelectJsonDocumentClick = onSelectJsonDocumentClick,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HttpMethodPicker(
    selectedMethod: HttpMethod?,
    onMethodSelected: (HttpMethod?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = selectedMethod?.name ?: stringResource(Res.string.any_method_button)
    val options = listOf<HttpMethod?>(null) + HttpMethod.entries

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
            readOnly = true,
            singleLine = true,
            shape = EditorFieldShape,
            label = { Text(stringResource(Res.string.http_method_label)) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { method ->
                val label = method?.name ?: stringResource(Res.string.any_method_button)
                DropdownMenuItem(
                    text = {
                        Text(
                            text = label,
                            fontWeight = if (method == selectedMethod) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            },
                        )
                    },
                    onClick = {
                        onMethodSelected(method)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun JsonBodyInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    supportingText: String,
    onSelectJsonDocumentClick: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            minLines = 6,
            maxLines = 12,
            shape = EditorFieldShape,
            label = { Text(label) },
            supportingText = { Text(supportingText) },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace,
            ),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            FilledTonalButton(onClick = onSelectJsonDocumentClick) {
                Icon(
                    painter = painterResource(Res.drawable.ic_upload_file),
                    contentDescription = null,
                )
                Spacer(Modifier.width(8.dp))
                Text(stringResource(Res.string.select_json_document_button))
            }
        }
    }
}

@Composable
private fun EditorSectionCard(
    eyebrow: String,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ) {
                Text(
                    text = eyebrow,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = title,
                    modifier = Modifier.semantics { heading() },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            content()
        }
    }
}

@Composable
private fun CatchAllNotice() {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_rule),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = stringResource(Res.string.catch_all_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = stringResource(Res.string.catch_all_description),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun ErrorBanner(message: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { liveRegion = LiveRegionMode.Polite },
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_error),
                contentDescription = null,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CreateMockActionBar(onCreateClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding(),
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 3.dp,
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .widthIn(max = 1120.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                val compact = maxWidth < 600.dp
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Button(
                        onClick = onCreateClick,
                        shapes = ButtonDefaults.shapes(),
                        modifier = if (compact) {
                            Modifier
                                .fillMaxWidth()
                                .heightIn(min = 56.dp)
                        } else {
                            Modifier
                                .widthIn(min = 240.dp)
                                .heightIn(min = 56.dp)
                        },
                        contentPadding = ButtonDefaults.MediumContentPadding,
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_add),
                            contentDescription = null,
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(stringResource(Res.string.create_mock_button))
                    }
                }
            }
        }
    }
}

private val EditorFieldShape = RoundedCornerShape(16.dp)
