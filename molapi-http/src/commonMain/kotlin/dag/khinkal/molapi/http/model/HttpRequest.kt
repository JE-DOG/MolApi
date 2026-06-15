package dag.khinkal.molapi.http.model

import dag.khinkal.molapi.core.model.ApiRequest

public data class HttpRequest(
    public val url: String,
    public val method: HttpMethod,
    public val headers: Headers? = null,
    public val body: HttpBody? = null,
) : ApiRequest
