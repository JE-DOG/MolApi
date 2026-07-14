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
        > : ReadApiMockRegistry<Request, Matcher, Response> {

    public val idGenerator: ApiMockIdGenerator<Request, Matcher, Response>

    public val mocks: StateFlow<List<ApiMock<Request, Matcher, Response>>>

    public fun add(response: Response, matcher: Matcher)

    public fun remove(id: String): Boolean

    public fun clear()
}

public fun <
        Request : ApiRequest,
        Matcher : ApiRequestMatcher<Request>,
        Response : ApiResponse,
        > ApiMockRegistry<Request, Matcher, Response>.add(
    mock: ApiMock<Request, Matcher, Response>,
): Unit = add(
    response = mock.response,
    matcher = mock.matcher,
)
