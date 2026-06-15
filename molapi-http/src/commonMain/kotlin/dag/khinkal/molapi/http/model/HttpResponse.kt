package dag.khinkal.molapi.http.model

import dag.khinkal.molapi.core.model.ApiResponse

public data class HttpResponse(
    public val body: HttpBody? = null,
    public val headers: Headers? = null,
    public val statusCode: Int = 200,
) : ApiResponse
