package dag.khinkal.molapi.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "api_mocks")
internal data class RoomApiMockEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "matcher")
    val matcher: String,
    @ColumnInfo(name = "response")
    val response: String,
)

internal fun RoomApiMockEntity.toRecord(): RoomApiMockRecord =
    RoomApiMockRecord(
        id = id,
        matcher = matcher,
        response = response,
    )

internal fun RoomApiMockRecord.toEntity(): RoomApiMockEntity =
    RoomApiMockEntity(
        id = id,
        matcher = matcher,
        response = response,
    )
