package dag.khinkal.molapi

import android.content.Context
import androidx.room.Room
import dag.khinkal.molapi.room.MolApiRoomDatabase
import dag.khinkal.molapi.room.MolApiRoomDatabaseConstructor
import kotlinx.coroutines.CoroutineScope

public fun createAndroidRoomMolApiClientConfig(
    context: Context,
    isEnabled: Boolean,
    coroutineScope: CoroutineScope,
): MolApiClientConfig =
    MolApiClientConfig(
        isEnabled = isEnabled,
        registry = createRoomHttpMockRegistry(
            database = createAndroidMolApiRoomDatabase(context),
            coroutineScope = coroutineScope,
        ),
    )

private fun createAndroidMolApiRoomDatabase(context: Context): MolApiRoomDatabase =
    Room.databaseBuilder<MolApiRoomDatabase>(
        context = context.applicationContext,
        name = "molapi-mocks.db",
        factory = MolApiRoomDatabaseConstructor::initialize,
    ).build()
