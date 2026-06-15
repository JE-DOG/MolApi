package dag.khinkal.molapi.core.registry

import dag.khinkal.molapi.core.matcher.ApiRequestMatcher
import dag.khinkal.molapi.core.model.ApiMock
import dag.khinkal.molapi.core.model.ApiRequest
import dag.khinkal.molapi.core.model.ApiResponse
import kotlinx.coroutines.flow.StateFlow

public interface ApiMockRegistry<
        Request : ApiRequest,
        Matcher : ApiRequestMatcher<Request>,
        Response : ApiResponse
        > : ReadApiMockRegistry<Request, Matcher, Response> {

    public val mocks: StateFlow<List<ApiMock<Request, Matcher, Response>>>

    public fun add(mock: ApiMock<Request, Matcher, Response>)

    public fun remove(mock: ApiMock<Request, Matcher, Response>): Boolean

    public fun clear()
}