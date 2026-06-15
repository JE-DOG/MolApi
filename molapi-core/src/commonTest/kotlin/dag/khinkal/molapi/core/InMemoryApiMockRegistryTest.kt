package dag.khinkal.molapi.core

import dag.khinkal.molapi.core.matcher.ApiRequestMatcher
import dag.khinkal.molapi.core.model.ApiRequest
import dag.khinkal.molapi.core.model.ApiResponse
import dag.khinkal.molapi.core.model.BaseApiMock
import dag.khinkal.molapi.core.registry.impl.InMemoryApiMockRegistry
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class InMemoryApiMockRegistryTest {

    @Test
    fun addAndFindReturnsMatchingMock() {
        val mock = BaseApiMock(
            matcher = TestRequestMatcher("match"),
            response = TestResponse("response")
        )
        val registry = InMemoryApiMockRegistry<TestRequest, TestRequestMatcher, TestResponse>()

        registry.add(mock)

        assertEquals(mock, registry.find(TestRequest("match")))
    }

    @Test
    fun mocksReturnsRegisteredMocksInRegistrationOrder() {
        val firstMock = BaseApiMock(
            matcher = TestRequestMatcher("first"),
            response = TestResponse("first_response")
        )
        val secondMock = BaseApiMock(
            matcher = TestRequestMatcher("second"),
            response = TestResponse("second_response")
        )
        val registry = InMemoryApiMockRegistry<TestRequest, TestRequestMatcher, TestResponse>()

        registry.add(firstMock)
        registry.add(secondMock)

        assertEquals(listOf(firstMock, secondMock), registry.mocks.value)
    }

    @Test
    fun mocksStateFlowKeepsPreviousEmissionImmutable() {
        val mock = BaseApiMock(
            matcher = TestRequestMatcher("match"),
            response = TestResponse("response")
        )
        val registry = InMemoryApiMockRegistry<TestRequest, TestRequestMatcher, TestResponse>()

        registry.add(mock)
        val snapshot = registry.mocks.value
        registry.clear()

        assertEquals(listOf(mock), snapshot)
        assertEquals(emptyList(), registry.mocks.value)
    }

    @Test
    fun findReturnsNullWhenRegistryIsEmpty() {
        val registry = InMemoryApiMockRegistry<TestRequest, TestRequestMatcher, TestResponse>()

        assertNull(registry.find(TestRequest("match")))
    }

    @Test
    fun removeDeletesMocksByHashCode() {
        val mock = BaseApiMock(
            matcher = TestRequestMatcher("match"),
            response = TestResponse("response")
        )
        val registry = InMemoryApiMockRegistry<TestRequest, TestRequestMatcher, TestResponse>()
        registry.add(mock)

        assertTrue(registry.remove(mock))

        assertNull(registry.find(TestRequest("match")))
    }

    @Test
    fun clearRemovesAllMocks() {
        val mock = BaseApiMock(
            matcher = TestRequestMatcher("match"),
            response = TestResponse("response")
        )
        val registry = InMemoryApiMockRegistry<TestRequest, TestRequestMatcher, TestResponse>()
        registry.add(mock)

        registry.clear()

        assertNull(registry.find(TestRequest("match")))
    }

    private data class TestRequest(val value: String) : ApiRequest

    private data class TestResponse(val value: String) : ApiResponse

    private class TestRequestMatcher(
        private val expectedValue: String
    ) : ApiRequestMatcher<TestRequest> {

        override fun matches(request: TestRequest): Boolean = request.value == expectedValue
    }
}
