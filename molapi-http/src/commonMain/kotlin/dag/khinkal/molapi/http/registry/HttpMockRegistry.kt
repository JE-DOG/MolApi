package dag.khinkal.molapi.http.registry

import dag.khinkal.molapi.core.matcher.ApiRequestMatcher
import dag.khinkal.molapi.core.registry.ApiMockRegistry
import dag.khinkal.molapi.http.model.HttpRequest
import dag.khinkal.molapi.http.model.HttpResponse

public typealias HttpApiMockRegistry = ApiMockRegistry<
        HttpRequest,
        ApiRequestMatcher<HttpRequest>,
        HttpResponse,
        Any,
        >
