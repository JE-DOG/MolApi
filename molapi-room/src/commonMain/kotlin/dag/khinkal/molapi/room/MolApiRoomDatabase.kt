package dag.khinkal.molapi.room

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(
    entities = [RoomApiMockEntity::class],
    version = 1,
    exportSchema = true
)
@ConstructedBy(MolApiRoomDatabaseConstructor::class)
public abstract class MolApiRoomDatabase : RoomDatabase() {

    internal abstract fun apiMockDao(): RoomApiMockDao
}

@Suppress("KotlinNoActualForExpect")
public expect object MolApiRoomDatabaseConstructor : RoomDatabaseConstructor<MolApiRoomDatabase> {

    public override fun initialize(): MolApiRoomDatabase
}
