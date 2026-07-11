package dag.khinkal.molapi.http.editor.util.resultlauncher

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import dag.khinkal.molapi.http.editor.util.resultlauncher.model.FileContentType
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.stringWithContentsOfURL
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UIKit.UIViewController
import platform.UniformTypeIdentifiers.UTTypeJSON
import platform.darwin.NSObject

@Composable
internal actual fun rememberFileResultLauncher(
    onDocumentSelected: (String) -> Unit,
    onDocumentReadFailed: () -> Unit
): ResultLauncher<List<FileContentType>> {
    return remember(onDocumentReadFailed, onDocumentSelected) {
        IosFileResultLauncher(
            onDocumentSelected = onDocumentSelected,
            onDocumentReadFailed = onDocumentReadFailed,
        )
    }
}

@OptIn(ExperimentalForeignApi::class)
private class IosFileResultLauncher(
    private val onDocumentSelected: (String) -> Unit,
    private val onDocumentReadFailed: () -> Unit,
) : ResultLauncher<List<FileContentType>> {

    private val delegate = IosJsonDocumentPickerDelegate()

    override fun launch(input: List<FileContentType>) {
        delegate.onDocumentSelected = onDocumentSelected
        delegate.onDocumentReadFailed = onDocumentReadFailed

        val contentTypes = buildList {
            input.forEach { contentType ->
                contentType.toIos().forEach { iosContentType ->
                    add(iosContentType)
                }
            }
        }
        val picker = UIDocumentPickerViewController(
            forOpeningContentTypes = contentTypes,
            asCopy = true,
        )
        picker.delegate = delegate
        picker.allowsMultipleSelection = false

        val rootViewController = UIApplication.sharedApplication.rootViewController()
        if (rootViewController == null) {
            onDocumentReadFailed()
            return
        }

        rootViewController.presentViewController(
            viewControllerToPresent = picker,
            animated = true,
            completion = null,
        )
    }
}

private class IosJsonDocumentPickerDelegate :
    NSObject(),
    UIDocumentPickerDelegateProtocol {

    var onDocumentSelected: (String) -> Unit = {}

    var onDocumentReadFailed: () -> Unit = {}

    override fun documentPicker(
        controller: UIDocumentPickerViewController,
        didPickDocumentsAtURLs: List<*>,
    ) {
        val url = didPickDocumentsAtURLs.firstOrNull() as? NSURL
        val body = url?.readText()
        if (body == null) {
            onDocumentReadFailed()
        } else {
            onDocumentSelected(body)
        }
    }
}

private fun UIApplication.rootViewController(): UIViewController? {
    return keyWindow?.rootViewController
}

@OptIn(ExperimentalForeignApi::class)
private fun NSURL.readText(): String? {
    return runCatching {
        NSString.stringWithContentsOfURL(
            url = this,
            encoding = NSUTF8StringEncoding,
            error = null,
        )
    }.getOrNull()
}

private fun FileContentType.toIos(): List<platform.UniformTypeIdentifiers.UTType> = when (this) {
    FileContentType.JSON -> listOf(UTTypeJSON)
}
