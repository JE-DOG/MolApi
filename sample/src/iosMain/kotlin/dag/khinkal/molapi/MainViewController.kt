package dag.khinkal.molapi

import androidx.compose.ui.window.ComposeUIViewController
import dag.khinkal.molapi.http.editor.MolApiEditor
import dag.khinkal.molapi.http.editor.init
import platform.UIKit.UIViewController

public fun initializeMolApiEditor() {
    MolApiEditor.init()
}

public fun MainViewController(): UIViewController =
    ComposeUIViewController {
        App()
    }
