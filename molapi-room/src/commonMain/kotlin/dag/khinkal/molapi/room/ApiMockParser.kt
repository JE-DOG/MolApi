package dag.khinkal.molapi.room

import dag.khinkal.molapi.core.matcher.ApiRequestMatcher
import dag.khinkal.molapi.core.model.ApiRequest
import dag.khinkal.molapi.core.model.ApiResponse

public interface ApiMockParser<
        Request : ApiRequest,
        Matcher : ApiRequestMatcher<Request>,
        Response : ApiResponse,
        > {

    public fun encodeMatcher(matcher: Matcher): String

    public fun decodeMatcher(matcher: String): Matcher

    public fun encodeResponse(response: Response): String

    public fun decodeResponse(response: String): Response
}
