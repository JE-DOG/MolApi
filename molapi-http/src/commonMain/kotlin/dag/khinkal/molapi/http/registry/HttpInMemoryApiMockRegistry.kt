package dag.khinkal.molapi.http.registry

import dag.khinkal.molapi.core.matcher.ApiRequestMatcher
import dag.khinkal.molapi.core.registry.impl.InMemoryApiMockRegistry
import dag.khinkal.molapi.http.model.HttpRequest
import dag.khinkal.molapi.http.model.HttpResponse

public typealias HttpInMemoryApiMockRegistry = InMemoryApiMockRegistry<
        HttpRequest,
        ApiRequestMatcher<HttpRequest>,
        HttpResponse,
        >