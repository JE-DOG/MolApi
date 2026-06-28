package dag.khinkal.molapi.http.model

import dag.khinkal.molapi.core.model.ApiResponse

public data class HttpResponse(
    public val body: HttpBody? = null,
    public val headers: Headers? = null,
    public val statusCode: Int = 200,
) : ApiResponse {

    init {
        require(statusCode in 100..599) {
            "HTTP status code must be in range 100..599, but was $statusCode"
        }
    }
}
