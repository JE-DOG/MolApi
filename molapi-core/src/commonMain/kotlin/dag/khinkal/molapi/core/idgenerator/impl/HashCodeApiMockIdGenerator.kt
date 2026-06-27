package dag.khinkal.molapi.core.idgenerator.impl

import dag.khinkal.molapi.core.idgenerator.ApiMockIdGenerator
import dag.khinkal.molapi.core.matcher.ApiRequestMatcher
import dag.khinkal.molapi.core.model.ApiRequest
import dag.khinkal.molapi.core.model.ApiResponse

public class HashCodeApiMockIdGenerator<
        Request : ApiRequest,
        Matcher : ApiRequestMatcher<Request>,
        Response : ApiResponse,
        > : ApiMockIdGenerator<Request, Matcher, Response, Int> {

    public override fun generateId(matcher: Matcher, response: Response): Int {
        var result = matcher.hashCode()
        result = 31 * result + response.hashCode()
        return result
    }
}