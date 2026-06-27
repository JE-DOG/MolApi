package dag.khinkal.molapi.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
internal interface RoomApiMockDao {

    @Query("SELECT * FROM api_mocks")
    fun readAll(): Flow<List<RoomApiMockEntity>>

    @Insert
    suspend fun insert(entity: RoomApiMockEntity)

    @Query("DELETE FROM api_mocks WHERE id = :id")
    suspend fun deleteById(id: String): Int

    @Query("DELETE FROM api_mocks")
    suspend fun deleteAll()
}
