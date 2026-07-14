package dag.khinkal.molapi.http.editor

import android.content.Context
import androidx.room.Room
import dag.khinkal.molapi.room.MolApiRoomDatabase
import dag.khinkal.molapi.room.MolApiRoomDatabaseConstructor
import kotlinx.coroutines.CoroutineScope

public fun MolApiEditor.init(
    context: Context,
    databaseName: String = MOL_API_EDITOR_DATABASE_NAME,
    coroutineScope: CoroutineScope = createMolApiEditorCoroutineScope(),
) {
    init(
        database = Room.databaseBuilder<MolApiRoomDatabase>(
            context = context.applicationContext,
            name = databaseName,
            factory = MolApiRoomDatabaseConstructor::initialize,
        )
            .build(),
        coroutineScope = coroutineScope,
    )
}
