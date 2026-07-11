package dag.khinkal.molapi.http.editor.util.resultlauncher

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dag.khinkal.molapi.http.editor.util.resultlauncher.model.FileContentType

@Composable
internal actual fun rememberFileResultLauncher(
    onDocumentSelected: (String) -> Unit,
    onDocumentReadFailed: () -> Unit
): ResultLauncher<List<FileContentType>> {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = OpenDocument(),
    ) { uri ->
        if (uri == null) {
            return@rememberLauncherForActivityResult
        }

        val body = context.readText(uri)
        if (body == null) {
            onDocumentReadFailed()
        } else {
            onDocumentSelected(body)
        }
    }

    return remember(launcher) {
        object : ResultLauncher<List<FileContentType>> {

            override fun launch(input: List<FileContentType>) {
                val contentTypes = buildList {
                    input.forEach { contentType ->
                        contentType.toMimeType().forEach { mimeType ->
                            add(mimeType)
                        }
                    }
                }

                launcher.launch(contentTypes.toTypedArray())
            }
        }
    }
}

private fun FileContentType.toMimeType(): Array<String> = when (this) {
    FileContentType.JSON -> arrayOf("application/json", "text/json")
}

private fun Context.readText(uri: Uri): String? {
    return runCatching {
        contentResolver.openInputStream(uri)?.bufferedReader()?.use { reader ->
            reader.readText()
        }
    }
        .getOrNull()
}
