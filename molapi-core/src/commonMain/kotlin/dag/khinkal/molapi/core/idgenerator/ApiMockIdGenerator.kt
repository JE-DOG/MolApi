package dag.khinkal.molapi.core.idgenerator

import dag.khinkal.molapi.core.matcher.ApiRequestMatcher
import dag.khinkal.molapi.core.model.ApiRequest
import dag.khinkal.molapi.core.model.ApiResponse

public interface ApiMockIdGenerator<
        Request : ApiRequest,
        Matcher : ApiRequestMatcher<Request>,
        Response : ApiResponse,
        > {

    public fun generateId(matcher: Matcher, response: Response): String
}