package dag.khinkal.molapi.core.model

import dag.khinkal.molapi.core.matcher.ApiRequestMatcher

public interface ApiMock<
        Request : ApiRequest,
        Matcher : ApiRequestMatcher<Request>,
        Response : ApiResponse,
        out Id : Any,
        > {

    public val id: Id
    public val matcher: Matcher
    public val response: Response
}
