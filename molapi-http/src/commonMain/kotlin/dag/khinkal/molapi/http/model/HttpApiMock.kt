package dag.khinkal.molapi.http.model

import dag.khinkal.molapi.core.matcher.ApiRequestMatcher
import dag.khinkal.molapi.core.model.ApiMock

public interface HttpApiMock : ApiMock<
        HttpRequest,
        ApiRequestMatcher<HttpRequest>,
        HttpResponse,
        Any,
        >
