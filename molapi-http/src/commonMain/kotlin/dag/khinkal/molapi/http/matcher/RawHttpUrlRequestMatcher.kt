package dag.khinkal.molapi.http.matcher

import dag.khinkal.molapi.core.matcher.ApiRequestMatcher
import dag.khinkal.molapi.http.model.Headers
import dag.khinkal.molapi.http.model.HttpBody
import dag.khinkal.molapi.http.model.HttpMethod
import dag.khinkal.molapi.http.model.HttpRequest
import dag.khinkal.molapi.http.util.toRawUrl

public class RawHttpUrlRequestMatcher(
    public val rawUrl: String,
    public val method: HttpMethod? = null,
    public val body: HttpBody? = null,
    public val headers: Headers? = null,
) : ApiRequestMatcher<HttpRequest> {

    public override fun matches(request: HttpRequest): Boolean {
        if (method != null && request.method != method) {
            return false
        }
        if (!request.url.toRawUrl().contains(rawUrl)) {
            return false
        }
        if (headers != null && request.headers != headers) {
            return false
        }
        if (body != null && request.body != body) {
            return false
        }
        return true
    }
}
