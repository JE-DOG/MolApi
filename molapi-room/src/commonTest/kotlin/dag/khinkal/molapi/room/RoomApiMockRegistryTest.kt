package dag.khinkal.molapi.room

import dag.khinkal.molapi.core.matcher.ApiRequestMatcher
import dag.khinkal.molapi.core.model.ApiRequest
import dag.khinkal.molapi.core.model.ApiResponse
import dag.khinkal.molapi.core.model.BaseApiMock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RoomApiMockRegistryTest {

    @Test
    fun loadsPersistedMocksOnCreation() {
        val storage = FakeApiMockStorage(
            records = mutableListOf(
                RoomApiMockRecord(
                    id = "mock",
                    matcher = "match",
                    response = "persisted response"
                )
            )
        )

        val registry = RoomApiMockRegistry<TestRequest, TestRequestMatcher, TestResponse>(
            storage = storage,
            parser = TestRoomApiMockParser,
        )

        val mock = registry.find(TestRequest("match"))

        assertEquals(TestResponse("persisted response"), mock?.response)
    }

    @Test
    fun addPersistsAndFindsMock() {
        val storage = FakeApiMockStorage()
        val registry = RoomApiMockRegistry<TestRequest, TestRequestMatcher, TestResponse>(
            storage = storage,
            parser = TestRoomApiMockParser,
        )
        val mock = BaseApiMock(
            matcher = TestRequestMatcher("match"),
            response = TestResponse("response")
        )

        registry.add(mock)

        assertEquals(
            listOf(
                RoomApiMockRecord(
                    id = mock.hashCode().toString(),
                    matcher = "match",
                    response = "response"
                )
            ),
            storage.records
        )
        val found = registry.find(TestRequest("match"))
        assertEquals(TestRequestMatcher("match"), found?.matcher)
        assertEquals(TestResponse("response"), found?.response)
    }

    @Test
    fun addPersistsCurrentResponseValue() {
        val storage = FakeApiMockStorage()
        val registry = RoomApiMockRegistry<TestRequest, TestRequestMatcher, TestResponse>(
            storage = storage,
            parser = TestRoomApiMockParser,
        )
        val mock = BaseApiMock(
            matcher = TestRequestMatcher("match"),
            response = TestResponse("response")
        )
        registry.add(mock)

        assertEquals("response", storage.records.single().response)
    }

    @Test
    fun findReturnsFirstMatchingMockInInsertionOrder() {
        val storage = FakeApiMockStorage()
        val registry = RoomApiMockRegistry<TestRequest, TestRequestMatcher, TestResponse>(
            storage = storage,
            parser = TestRoomApiMockParser,
        )
        val first = BaseApiMock(
            matcher = TestRequestMatcher("match"),
            response = TestResponse("first response")
        )
        val second = BaseApiMock(
            matcher = TestRequestMatcher("match"),
            response = TestResponse("second response")
        )

        registry.add(first)
        registry.add(second)

        val found = registry.find(TestRequest("match"))
        assertEquals(TestRequestMatcher("match"), found?.matcher)
        assertEquals(TestResponse("first response"), found?.response)
    }

    @Test
    fun removeDeletesMockByHashCode() {
        val storage = FakeApiMockStorage()
        val registry = RoomApiMockRegistry<TestRequest, TestRequestMatcher, TestResponse>(
            storage = storage,
            parser = TestRoomApiMockParser,
        )
        val first = BaseApiMock(
            matcher = TestRequestMatcher("first"),
            response = TestResponse("first response")
        )
        val second = BaseApiMock(
            matcher = TestRequestMatcher("second"),
            response = TestResponse("second response")
        )

        registry.add(first)
        registry.add(second)

        assertTrue(registry.remove(first.hashCode()))

        assertNull(registry.find(TestRequest("first")))
        assertEquals(
            TestResponse("second response"),
            registry.find(TestRequest("second"))?.response
        )
        assertEquals(
            listOf(second.hashCode().toString()),
            storage.records.map { record -> record.id })
    }

    @Test
    fun clearRemovesAllMocks() {
        val storage = FakeApiMockStorage()
        val registry = RoomApiMockRegistry<TestRequest, TestRequestMatcher, TestResponse>(
            storage = storage,
            parser = TestRoomApiMockParser,
        )
        registry.add(
            BaseApiMock(
                matcher = TestRequestMatcher("match"),
                response = TestResponse("response")
            )
        )

        registry.clear()

        assertNull(registry.find(TestRequest("match")))
        assertEquals(emptyList(), storage.records)
    }

    private data class TestRequest(val value: String) : ApiRequest

    private data class TestResponse(val value: String) : ApiResponse

    private data class TestRequestMatcher(
        val expectedValue: String
    ) : ApiRequestMatcher<TestRequest> {

        override fun matches(request: TestRequest): Boolean = request.value == expectedValue
    }

    private object TestRoomApiMockParser :
        ApiMockParser<TestRequest, TestRequestMatcher, TestResponse> {

        override fun encodeMatcher(matcher: TestRequestMatcher): String = matcher.expectedValue

        override fun decodeMatcher(matcher: String): TestRequestMatcher =
            TestRequestMatcher(matcher)

        override fun encodeResponse(response: TestResponse): String = response.value

        override fun decodeResponse(response: String): TestResponse = TestResponse(response)

        override fun decodeRecord(
            record: RoomApiMockRecord,
        ): BaseApiMock<TestRequest, TestRequestMatcher, TestResponse> =
            BaseApiMock(
                matcher = decodeMatcher(record.matcher),
                response = decodeResponse(record.response),
            )
    }

    private class FakeApiMockStorage(
        val records: MutableList<RoomApiMockRecord> = mutableListOf()
    ) : ApiMockStorage {

        override fun observeAll(): List<RoomApiMockRecord> = records.toList()

        override fun add(record: RoomApiMockRecord) {
            records.add(record)
        }

        override fun remove(id: String): Boolean {
            val sizeBeforeRemove = records.size
            records.removeAll { record -> record.id == id }
            return records.size != sizeBeforeRemove
        }

        override fun clear() {
            records.clear()
        }
    }
}
