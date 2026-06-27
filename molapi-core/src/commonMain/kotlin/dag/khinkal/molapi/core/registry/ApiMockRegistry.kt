package dag.khinkal.molapi.core.registry

import dag.khinkal.molapi.core.idgenerator.ApiMockIdGenerator
import dag.khinkal.molapi.core.matcher.ApiRequestMatcher
import dag.khinkal.molapi.core.model.ApiMock
import dag.khinkal.molapi.core.model.ApiRequest
import dag.khinkal.molapi.core.model.ApiResponse
import kotlinx.coroutines.flow.StateFlow

public interface ApiMockRegistry<
        Request : ApiRequest,
        Matcher : ApiRequestMatcher<Request>,
        Response : ApiResponse,
        Id : Any,
        > : ReadApiMockRegistry<Request, Matcher, Response, Id> {

    public val idGenerator: ApiMockIdGenerator<Request, Matcher, Response, Id>

    public val mocks: StateFlow<List<ApiMock<Request, Matcher, Response, Id>>>

    public fun add(response: Response, matcher: Matcher)

    public fun remove(id: Id): Boolean

    public fun clear()
}

public fun <
        Request : ApiRequest,
        Matcher : ApiRequestMatcher<Request>,
        Response : ApiResponse,
        Id : Any,
        > ApiMockRegistry<Request, Matcher, Response, Id>.add(
    mock: ApiMock<Request, Matcher, Response, Id>,
): Unit = add(
    response = mock.response,
    matcher = mock.matcher,
)
