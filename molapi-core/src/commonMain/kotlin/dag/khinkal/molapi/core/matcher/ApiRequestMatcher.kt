package dag.khinkal.molapi.core.matcher

import dag.khinkal.molapi.core.model.ApiRequest

public interface ApiRequestMatcher<AR : ApiRequest> {

    public fun matches(request: AR): Boolean
}