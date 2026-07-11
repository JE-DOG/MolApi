package dag.khinkal.molapi.http.editor.util.resultlauncher

import androidx.compose.runtime.Composable
import dag.khinkal.molapi.http.editor.util.resultlauncher.model.FileContentType

@Composable
internal expect fun rememberFileResultLauncher(
    onDocumentSelected: (String) -> Unit,
    onDocumentReadFailed: () -> Unit,
): ResultLauncher<List<FileContentType>>
