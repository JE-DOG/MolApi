package dag.khinkal.molapi.http.editor

import androidx.annotation.VisibleForTesting
import dag.khinkal.molapi.http.registry.HttpApiMockRegistry
import dag.khinkal.molapi.room.MolApiRoomDatabase
import dag.khinkal.molapi.room.RoomApiMockRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob

internal const val MOL_API_EDITOR_DATABASE_NAME: String = "molapi_mocks.db"

public object MolApiEditor {

    private var initializedRegistry: HttpApiMockRegistry? = null

    public val registry: HttpApiMockRegistry
        get() = initializedRegistry
            ?: error(
                "MolApiEditor.init must be called before using MolApiEditor.registry",
            )

    public fun init(registry: HttpApiMockRegistry) {
        initializedRegistry = registry
    }

    public fun init(
        database: MolApiRoomDatabase,
        coroutineScope: CoroutineScope = createMolApiEditorCoroutineScope(),
    ): Unit = init(
        registry = RoomApiMockRegistry(
            database = database,
            parser = HttpRoomApiMockParser,
            coroutineScope = coroutineScope,
        ),
    )

    @VisibleForTesting
    internal fun resetForTests() {
        initializedRegistry = null
    }
}

internal fun createMolApiEditorCoroutineScope(): CoroutineScope =
    CoroutineScope(Dispatchers.IO + SupervisorJob())
