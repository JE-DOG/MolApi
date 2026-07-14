package dag.khinkal.molapi.core.model

import dag.khinkal.molapi.core.matcher.ApiRequestMatcher

public interface ApiMock<
        Request : ApiRequest,
        Matcher : ApiRequestMatcher<Request>,
        Response : ApiResponse,
        > {

    public val id: String
    public val matcher: Matcher
    public val response: Response
}
