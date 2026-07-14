package dag.khinkal.molapi.http.editor

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import dag.khinkal.molapi.http.editor.create.CreateHttpMockScreen
import dag.khinkal.molapi.http.editor.list.HttpMockListScreen
import dag.khinkal.molapi.http.registry.HttpApiMockRegistry

@Composable
public fun MolApiHttpMockEditorScreen(
    registry: () -> HttpApiMockRegistry = MolApiEditor::registry,
    modifier: Modifier = Modifier,
) {
    val viewModel = viewModel<HttpMockEditorViewModel> {
        HttpMockEditorViewModel(registry())
    }

    MaterialTheme(motionScheme = MotionScheme.expressive()) {
        HttpMockEditorScreen(
            viewModel = viewModel,
            modifier = modifier,
        )
    }
}

@Composable
internal fun HttpMockEditorScreen(
    viewModel: HttpMockEditorViewModel,
    modifier: Modifier = Modifier,
) {
    val motionScheme = MaterialTheme.motionScheme

    AnimatedContent(
        targetState = viewModel.destination,
        modifier = modifier,
        transitionSpec = {
            val direction = if (targetState == HttpMockEditorDestination.Create) 1 else -1
            val enter = fadeIn(motionScheme.defaultEffectsSpec()) +
                    slideInHorizontally(motionScheme.defaultSpatialSpec()) { width ->
                        direction * width / 4
                    }
            val exit = fadeOut(motionScheme.fastEffectsSpec()) +
                    slideOutHorizontally(motionScheme.fastSpatialSpec()) { width ->
                        -direction * width / 4
                    }
            enter togetherWith exit
        },
        contentKey = { destination -> destination },
    ) { destination ->
        when (destination) {
            HttpMockEditorDestination.List -> HttpMockListScreen(viewModel = viewModel)
            HttpMockEditorDestination.Create -> CreateHttpMockScreen(viewModel = viewModel)
        }
    }
}
