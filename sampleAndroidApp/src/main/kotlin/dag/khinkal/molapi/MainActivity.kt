package dag.khinkal.molapi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope

public class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val isMolApiEnabled = BuildConfig.DEBUG || BuildConfig.BUILD_TYPE == "beta"
            App(
                molApiConfig = createAndroidRoomMolApiClientConfig(
                    context = applicationContext,
                    isEnabled = isMolApiEnabled,
                    coroutineScope = lifecycleScope,
                ),
            )
        }
    }
}

@Preview
@Composable
public fun AppAndroidPreview() {
    App()
}
