package dag.khinkal.molapi.core.registry.impl

import dag.khinkal.molapi.core.matcher.ApiRequestMatcher
import dag.khinkal.molapi.core.model.ApiMock
import dag.khinkal.molapi.core.model.ApiRequest
import dag.khinkal.molapi.core.model.ApiResponse
import dag.khinkal.molapi.core.registry.ApiMockRegistry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

public class InMemoryApiMockRegistry<
        Request : ApiRequest,
        Matcher : ApiRequestMatcher<Request>,
        Response : ApiResponse
        > : ApiMockRegistry<Request, Matcher, Response> {

    private val _mocks = MutableStateFlow<List<ApiMock<Request, Matcher, Response>>>(emptyList())

    public override val mocks: StateFlow<List<ApiMock<Request, Matcher, Response>>> =
        _mocks.asStateFlow()

    public override fun add(mock: ApiMock<Request, Matcher, Response>) {
        _mocks.value += mock
    }

    override fun remove(mock: ApiMock<Request, Matcher, Response>): Boolean {
        val currentMocks = _mocks.value
        val updatedMocks = currentMocks.filterNot { it == mock }

        _mocks.value = updatedMocks

        return updatedMocks.size != currentMocks.size
    }

    public override fun clear() {
        _mocks.value = emptyList()
    }

    public override fun find(request: Request): ApiMock<Request, Matcher, Response>? =
        mocks.value.firstOrNull { mock -> mock.matcher.matches(request) }
}