package dag.khinkal.molapi.http.editor

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import dag.khinkal.molapi.room.MolApiRoomDatabase
import kotlinx.coroutines.CoroutineScope
import platform.Foundation.NSHomeDirectory

public fun MolApiEditor.init(
    databaseName: String = MOL_API_EDITOR_DATABASE_NAME,
    coroutineScope: CoroutineScope = createMolApiEditorCoroutineScope(),
) {
    val databasePath = NSHomeDirectory() + "/Documents/MolApi/Mocks/$databaseName"
    init(
        database = Room.databaseBuilder<MolApiRoomDatabase>(
            name = databasePath,
        )
            .setDriver(BundledSQLiteDriver())
            .build(),
        coroutineScope = coroutineScope,
    )
}
