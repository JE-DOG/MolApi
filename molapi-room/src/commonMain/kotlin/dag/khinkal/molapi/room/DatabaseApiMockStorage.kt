package dag.khinkal.molapi.room

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

internal interface ApiMockStorage {

    fun observeAll(): Flow<List<RoomApiMockRecord>>

    fun add(record: RoomApiMockRecord)

    fun remove(
        response: String,
        matcher: String,
    ): Boolean

    fun clear()
}

internal class DatabaseApiMockStorage(
    private val dao: RoomApiMockDao,
) : ApiMockStorage {

    override fun observeAll(): Flow<List<RoomApiMockRecord>> =
        dao.readAll()
            .map { it.map { entity -> entity.toRecord() } }

    override fun add(record: RoomApiMockRecord) = runBlocking {
        dao.insert(record.toEntity())
    }

    override fun remove(
        response: String,
        matcher: String,
    ): Boolean = runBlocking {
        val result = dao.deleteBy(
            matcher = matcher,
            response = response,
        )

        result > 0
    }

    override fun clear() = runBlocking {
        dao.deleteAll()
    }
}
