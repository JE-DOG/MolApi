package dag.khinkal.molapi.core.idgenerator.impl

import dag.khinkal.molapi.core.idgenerator.ApiMockIdGenerator
import dag.khinkal.molapi.core.matcher.ApiRequestMatcher
import dag.khinkal.molapi.core.model.ApiRequest
import dag.khinkal.molapi.core.model.ApiResponse
import kotlin.uuid.Uuid

public class UuidApiMockIdGenerator<
        Request : ApiRequest,
        Matcher : ApiRequestMatcher<Request>,
        Response : ApiResponse,
        > : ApiMockIdGenerator<Request, Matcher, Response> {

    public override fun generateId(matcher: Matcher, response: Response): String {
        return Uuid.random().toString()
    }
}
