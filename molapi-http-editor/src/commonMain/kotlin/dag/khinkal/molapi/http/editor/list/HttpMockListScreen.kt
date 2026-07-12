package dag.khinkal.molapi.http.editor.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumExtendedFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dag.khinkal.molapi.http.editor.HttpMockEditorMock
import dag.khinkal.molapi.http.editor.HttpMockEditorViewModel
import molapi.molapi_http_editor.generated.resources.Res
import molapi.molapi_http_editor.generated.resources.add_mock_button
import molapi.molapi_http_editor.generated.resources.any_method_button
import molapi.molapi_http_editor.generated.resources.any_url_label
import molapi.molapi_http_editor.generated.resources.cancel_button
import molapi.molapi_http_editor.generated.resources.clear_search_button
import molapi.molapi_http_editor.generated.resources.clear_search_description
import molapi.molapi_http_editor.generated.resources.collapse_content_button
import molapi.molapi_http_editor.generated.resources.confirm_delete_button
import molapi.molapi_http_editor.generated.resources.custom_matcher_label
import molapi.molapi_http_editor.generated.resources.delete_all_description
import molapi.molapi_http_editor.generated.resources.delete_all_dialog_message
import molapi.molapi_http_editor.generated.resources.delete_all_dialog_title
import molapi.molapi_http_editor.generated.resources.delete_button
import molapi.molapi_http_editor.generated.resources.delete_mock_dialog_message
import molapi.molapi_http_editor.generated.resources.delete_mock_dialog_title
import molapi.molapi_http_editor.generated.resources.expand_content_button
import molapi.molapi_http_editor.generated.resources.filtered_mocks_count
import molapi.molapi_http_editor.generated.resources.find_mocks_label
import molapi.molapi_http_editor.generated.resources.find_mocks_placeholder
import molapi.molapi_http_editor.generated.resources.http_mocks_title
import molapi.molapi_http_editor.generated.resources.ic_add
import molapi.molapi_http_editor.generated.resources.ic_close
import molapi.molapi_http_editor.generated.resources.ic_delete
import molapi.molapi_http_editor.generated.resources.ic_expand_less
import molapi.molapi_http_editor.generated.resources.ic_expand_more
import molapi.molapi_http_editor.generated.resources.ic_more_vert
import molapi.molapi_http_editor.generated.resources.ic_rule
import molapi.molapi_http_editor.generated.resources.ic_search
import molapi.molapi_http_editor.generated.resources.more_options_description
import molapi.molapi_http_editor.generated.resources.no_mocks_description
import molapi.molapi_http_editor.generated.resources.no_mocks_title
import molapi.molapi_http_editor.generated.resources.no_search_results_description
import molapi.molapi_http_editor.generated.resources.no_search_results_title
import molapi.molapi_http_editor.generated.resources.request_body_title
import molapi.molapi_http_editor.generated.resources.request_headers_title
import molapi.molapi_http_editor.generated.resources.request_title
import molapi.molapi_http_editor.generated.resources.response_body_title
import molapi.molapi_http_editor.generated.resources.response_headers_title
import molapi.molapi_http_editor.generated.resources.response_title
import molapi.molapi_http_editor.generated.resources.status_code_label
import molapi.molapi_http_editor.generated.resources.visible_mocks_count
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun HttpMockListScreen(
    viewModel: HttpMockEditorViewModel,
    modifier: Modifier = Modifier,
) {
    val mocks by viewModel.mocks.collectAsStateWithLifecycle()
    val visibleMocks = viewModel.visibleMocks(mocks)
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var pendingDeletion by remember { mutableStateOf<HttpMockEditorMock?>(null) }
    var showDeleteAllDialog by remember { mutableStateOf(false) }
    var showMoreOptions by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(stringResource(Res.string.http_mocks_title)) },
                subtitle = {
                    Text(
                        if (viewModel.searchQuery.isBlank()) {
                            stringResource(Res.string.visible_mocks_count, mocks.size)
                        } else {
                            stringResource(
                                Res.string.filtered_mocks_count,
                                visibleMocks.size,
                                mocks.size,
                            )
                        },
                    )
                },
                actions = {
                    if (mocks.isNotEmpty()) {
                        IconButton(onClick = { showMoreOptions = true }) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_more_vert),
                                contentDescription = stringResource(
                                    Res.string.more_options_description,
                                ),
                            )
                        }
                        DropdownMenu(
                            expanded = showMoreOptions,
                            onDismissRequest = { showMoreOptions = false },
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(Res.string.delete_all_description)) },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(Res.drawable.ic_delete),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                    )
                                },
                                onClick = {
                                    showMoreOptions = false
                                    showDeleteAllDialog = true
                                },
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            if (mocks.isNotEmpty()) {
                MediumExtendedFloatingActionButton(onClick = viewModel::showCreateMock) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_add),
                        contentDescription = null,
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(stringResource(Res.string.add_mock_button))
                }
            }
        },
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentPadding = PaddingValues(
                start = 16.dp,
                top = 12.dp,
                end = 16.dp,
                bottom = 112.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (mocks.isNotEmpty()) {
                item(key = "search") {
                    CenteredContent {
                        MockSearchField(
                            query = viewModel.searchQuery,
                            onQueryChange = viewModel::onSearchQueryChanged,
                            onClear = { viewModel.onSearchQueryChanged("") },
                        )
                    }
                }
            }

            when {
                mocks.isEmpty() -> item(key = "empty") {
                    CenteredContent {
                        EmptyState(
                            title = stringResource(Res.string.no_mocks_title),
                            description = stringResource(Res.string.no_mocks_description),
                            action = {
                                Button(
                                    onClick = viewModel::showCreateMock,
                                    shapes = ButtonDefaults.shapes(),
                                ) {
                                    Icon(
                                        painter = painterResource(Res.drawable.ic_add),
                                        contentDescription = null,
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(stringResource(Res.string.add_mock_button))
                                }
                            },
                        )
                    }
                }

                visibleMocks.isEmpty() -> item(key = "no-results") {
                    CenteredContent {
                        EmptyState(
                            title = stringResource(Res.string.no_search_results_title),
                            description = stringResource(
                                Res.string.no_search_results_description,
                            ),
                            action = {
                                FilledTonalButton(
                                    onClick = { viewModel.onSearchQueryChanged("") },
                                ) {
                                    Text(stringResource(Res.string.clear_search_button))
                                }
                            },
                        )
                    }
                }

                else -> itemsIndexed(
                    items = visibleMocks,
                    key = { index, mock -> "$index:${mock.id.hashCode()}" },
                ) { _, mock ->
                    CenteredContent {
                        HttpMockCard(
                            mock = mock,
                            onDeleteClick = { pendingDeletion = mock },
                        )
                    }
                }
            }
        }
    }

    pendingDeletion?.let { mock ->
        DeleteMockDialog(
            mock = mock,
            onConfirm = {
                viewModel.removeMock(mock)
                pendingDeletion = null
            },
            onDismiss = { pendingDeletion = null },
        )
    }
    if (showDeleteAllDialog) {
        DeleteAllMocksDialog(
            mockCount = mocks.size,
            onConfirm = {
                viewModel.clearMocks()
                showDeleteAllDialog = false
            },
            onDismiss = { showDeleteAllDialog = false },
        )
    }
}

@Composable
private fun CenteredContent(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 840.dp)
                .fillMaxWidth(),
        ) {
            content()
        }
    }
}

@Composable
private fun MockSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val searchDescription = stringResource(Res.string.find_mocks_label)

    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = searchDescription },
        singleLine = true,
        shape = CircleShape,
        placeholder = { Text(stringResource(Res.string.find_mocks_placeholder)) },
        leadingIcon = {
            Icon(
                painter = painterResource(Res.drawable.ic_search),
                contentDescription = null,
            )
        },
        trailingIcon = if (query.isNotEmpty()) {
            {
                IconButton(onClick = onClear) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_close),
                        contentDescription = stringResource(
                            Res.string.clear_search_description,
                        ),
                    )
                }
            }
        } else {
            null
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
    )
}

@Composable
private fun EmptyState(
    title: String,
    description: String,
    action: @Composable () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 320.dp),
        shape = RoundedCornerShape(36.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_rule),
                    contentDescription = null,
                    modifier = Modifier.padding(18.dp).size(40.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            Spacer(Modifier.size(20.dp))
            Text(
                text = title,
                modifier = Modifier.semantics { heading() },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.size(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.size(24.dp))
            action()
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HttpMockCard(
    mock: HttpMockEditorMock,
    onDeleteClick: () -> Unit,
) {
    var expanded by remember(mock.id) { mutableStateOf(false) }
    val requestSections = mock.requestSections()
    val responseSections = mock.responseSections()
    val hasDetails = requestSections.isNotEmpty() || responseSections.isNotEmpty()
    val motionScheme = MaterialTheme.motionScheme
    val methodText = when {
        mock.isCustomMatcher -> stringResource(Res.string.custom_matcher_label)
        mock.method == null -> stringResource(Res.string.any_method_button)
        else -> mock.method.name
    }
    val matcherText = when {
        mock.isCustomMatcher -> mock.matcherDescription
        mock.urlText == null -> stringResource(Res.string.any_url_label)
        else -> mock.urlText
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                LabelBadge(
                    text = methodText,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                )
                val statusColors = statusBadgeColors(mock.statusCode)
                LabelBadge(
                    text = stringResource(Res.string.status_code_label, mock.statusCode),
                    containerColor = statusColors.container,
                    contentColor = statusColors.content,
                )
            }

            Text(
                text = matcherText,
                style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Monospace),
                fontWeight = FontWeight.Medium,
                maxLines = if (expanded) 3 else 2,
                overflow = TextOverflow.Ellipsis,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (hasDetails) {
                    TextButton(onClick = { expanded = !expanded }) {
                        Icon(
                            painter = painterResource(
                                if (expanded) Res.drawable.ic_expand_less
                                else Res.drawable.ic_expand_more,
                            ),
                            contentDescription = null,
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            stringResource(
                                if (expanded) Res.string.collapse_content_button
                                else Res.string.expand_content_button,
                            ),
                        )
                    }
                }
                Spacer(Modifier.weight(1f))
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_delete),
                        contentDescription = stringResource(Res.string.delete_button),
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }

            AnimatedVisibility(
                visible = expanded && hasDetails,
                enter = expandVertically(animationSpec = motionScheme.defaultSpatialSpec()) +
                        fadeIn(animationSpec = motionScheme.defaultEffectsSpec()),
                exit = shrinkVertically(animationSpec = motionScheme.fastSpatialSpec()) +
                        fadeOut(animationSpec = motionScheme.fastEffectsSpec()),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    SelectionContainer {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            if (requestSections.isNotEmpty()) {
                                DetailGroup(
                                    title = stringResource(Res.string.request_title),
                                    sections = requestSections,
                                )
                            }
                            if (responseSections.isNotEmpty()) {
                                DetailGroup(
                                    title = stringResource(Res.string.response_title),
                                    sections = responseSections,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LabelBadge(
    text: String,
    containerColor: Color,
    contentColor: Color,
) {
    Surface(
        shape = CircleShape,
        color = containerColor,
        contentColor = contentColor,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun DetailGroup(
    title: String,
    sections: List<MockContentSection>,
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = title,
                modifier = Modifier.semantics { heading() },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            sections.forEach { section ->
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = stringResource(section.title),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 220.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .verticalScroll(rememberScrollState())
                            .padding(12.dp),
                    ) {
                        Text(
                            text = section.content,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                            ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun statusBadgeColors(statusCode: Int): BadgeColors = when (statusCode / 100) {
    2 -> BadgeColors(
        container = MaterialTheme.colorScheme.tertiaryContainer,
        content = MaterialTheme.colorScheme.onTertiaryContainer,
    )

    4, 5 -> BadgeColors(
        container = MaterialTheme.colorScheme.errorContainer,
        content = MaterialTheme.colorScheme.onErrorContainer,
    )

    else -> BadgeColors(
        container = MaterialTheme.colorScheme.primaryContainer,
        content = MaterialTheme.colorScheme.onPrimaryContainer,
    )
}

@Composable
private fun DeleteMockDialog(
    mock: HttpMockEditorMock,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val matcherText = mock.urlText
        ?: if (mock.isCustomMatcher) {
            stringResource(Res.string.custom_matcher_label)
        } else {
            stringResource(Res.string.any_url_label)
        }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                painter = painterResource(Res.drawable.ic_delete),
                contentDescription = null,
            )
        },
        title = { Text(stringResource(Res.string.delete_mock_dialog_title)) },
        text = {
            Text(stringResource(Res.string.delete_mock_dialog_message, matcherText))
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error,
                ),
            ) {
                Text(stringResource(Res.string.confirm_delete_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.cancel_button))
            }
        },
    )
}

@Composable
private fun DeleteAllMocksDialog(
    mockCount: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                painter = painterResource(Res.drawable.ic_delete),
                contentDescription = null,
            )
        },
        title = { Text(stringResource(Res.string.delete_all_dialog_title)) },
        text = {
            Text(stringResource(Res.string.delete_all_dialog_message, mockCount))
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error,
                ),
            ) {
                Text(stringResource(Res.string.confirm_delete_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.cancel_button))
            }
        },
    )
}

private data class BadgeColors(
    val container: Color,
    val content: Color,
)

private data class MockContentSection(
    val title: StringResource,
    val content: String,
)

private fun HttpMockEditorMock.requestSections(): List<MockContentSection> = buildList {
    if (isCustomMatcher) {
        add(MockContentSection(Res.string.custom_matcher_label, matcherDescription))
    }
    requestHeaders?.takeIf(String::isNotBlank)?.let {
        add(MockContentSection(Res.string.request_headers_title, it))
    }
    requestBody?.takeIf(String::isNotBlank)?.let {
        add(MockContentSection(Res.string.request_body_title, it))
    }
}

private fun HttpMockEditorMock.responseSections(): List<MockContentSection> = buildList {
    responseHeaders?.takeIf(String::isNotBlank)?.let {
        add(MockContentSection(Res.string.response_headers_title, it))
    }
    responseBody?.takeIf(String::isNotBlank)?.let {
        add(MockContentSection(Res.string.response_body_title, it))
    }
}
