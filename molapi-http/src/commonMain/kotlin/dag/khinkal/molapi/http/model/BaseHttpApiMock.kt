package dag.khinkal.molapi.http.model

import dag.khinkal.molapi.core.matcher.ApiRequestMatcher

public data class BaseHttpApiMock(
    public override val matcher: ApiRequestMatcher<HttpRequest>,
    public override val response: HttpResponse,
) : HttpApiMock