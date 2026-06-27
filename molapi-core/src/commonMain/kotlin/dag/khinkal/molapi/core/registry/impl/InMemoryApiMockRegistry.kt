package dag.khinkal.molapi.core.registry.impl

import dag.khinkal.molapi.core.idgenerator.ApiMockIdGenerator
import dag.khinkal.molapi.core.matcher.ApiRequestMatcher
import dag.khinkal.molapi.core.model.ApiMock
import dag.khinkal.molapi.core.model.ApiRequest
import dag.khinkal.molapi.core.model.ApiResponse
import dag.khinkal.molapi.core.model.BaseApiMock
import dag.khinkal.molapi.core.registry.ApiMockRegistry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet

public open class InMemoryApiMockRegistry<
        Request : ApiRequest,
        Matcher : ApiRequestMatcher<Request>,
        Response : ApiResponse,
        Id : Any,
        >(
    public override val idGenerator: ApiMockIdGenerator<Request, Matcher, Response, Id>,
) : ApiMockRegistry<Request, Matcher, Response, Id> {

    private val _mocks =
        MutableStateFlow<List<ApiMock<Request, Matcher, Response, Id>>>(emptyList())

    public override val mocks: StateFlow<List<ApiMock<Request, Matcher, Response, Id>>> =
        _mocks.asStateFlow()

    public override fun add(
        response: Response,
        matcher: Matcher
    ) {
        val mock = BaseApiMock(
            id = idGenerator.generateId(matcher = matcher, response = response),
            matcher = matcher,
            response = response,
        )

        _mocks.update { it + mock }
    }

    public override fun remove(id: Id): Boolean {
        val currentMocks = _mocks.value

        val updatedMocks = _mocks.updateAndGet { mocks ->
            mocks.filterNot { it.id == id }
        }

        return updatedMocks.size != currentMocks.size
    }

    public override fun clear() {
        _mocks.value = emptyList()
    }

    public override fun find(request: Request): ApiMock<Request, Matcher, Response, Id>? =
        mocks.value.firstOrNull { mock -> mock.matcher.matches(request) }
}
