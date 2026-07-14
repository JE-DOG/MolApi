package dag.khinkal.molapi.http.model

import dag.khinkal.molapi.core.idgenerator.impl.UuidApiMockIdGenerator
import dag.khinkal.molapi.core.matcher.ApiRequestMatcher

public data class BaseHttpApiMock(
    public override val matcher: ApiRequestMatcher<HttpRequest>,
    public override val response: HttpResponse,
    public override val id: String =
        UuidApiMockIdGenerator<HttpRequest, ApiRequestMatcher<HttpRequest>, HttpResponse>()
            .generateId(matcher = matcher, response = response),
) : HttpApiMock
