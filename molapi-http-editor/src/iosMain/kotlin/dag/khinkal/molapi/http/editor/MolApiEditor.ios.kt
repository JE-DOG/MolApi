package dag.khinkal.molapi.http.editor

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import dag.khinkal.molapi.room.MolApiRoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

private const val DATABASE_DIRECTORY: String = "Documents/Databases"

public fun MolApiEditor.init(
    databaseName: String = MOL_API_EDITOR_DATABASE_NAME,
    coroutineScope: CoroutineScope = createMolApiEditorCoroutineScope(),
) {
    val databasePath = createDatabasePath(databaseName)
    init(
        database = Room.databaseBuilder<MolApiRoomDatabase>(
            name = databasePath,
        )
            .setDriver(BundledSQLiteDriver())
            .build(),
        coroutineScope = coroutineScope,
    )
}

@OptIn(ExperimentalForeignApi::class)
internal fun createDatabasePath(name: String): String {
    require(name.isNotBlank()) {
        "Cannot build a database with an empty name"
    }

    val fileManager = NSFileManager.defaultManager
    val applicationSupportDirectory = requireNotNull(
        fileManager.URLForDirectory(
            directory = NSApplicationSupportDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = true,
            error = null,
        ),
    ) {
        "Unable to locate the Application Support directory"
    }

    val databaseDirectory = requireNotNull(
        applicationSupportDirectory.URLByAppendingPathComponent(DATABASE_DIRECTORY),
    ) {
        "Unable to build the database directory URL"
    }
    val databaseDirectoryPath = requireNotNull(databaseDirectory.path) {
        "Unable to resolve the database directory path"
    }

    val directoryCreated = fileManager.createDirectoryAtURL(
        url = databaseDirectory,
        withIntermediateDirectories = true,
        attributes = null,
        error = null,
    )
    check(directoryCreated) {
        "Unable to create the database directory at '$databaseDirectoryPath'"
    }

    return requireNotNull(
        databaseDirectory.URLByAppendingPathComponent(name)?.path,
    ) {
        "Unable to build the database path for '$name'"
    }
}
