package dag.khinkal.molapi.core.model

import dag.khinkal.molapi.core.matcher.ApiRequestMatcher

public data class BaseApiMock<
        Request : ApiRequest,
        Matcher : ApiRequestMatcher<Request>,
        Response : ApiResponse,
        Id : Any,
        >(

    public override val id: Id,
    public override val matcher: Matcher,
    public override val response: Response,
) : ApiMock<Request, Matcher, Response, Id>
