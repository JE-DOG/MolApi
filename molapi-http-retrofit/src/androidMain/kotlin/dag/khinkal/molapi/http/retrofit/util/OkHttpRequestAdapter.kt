package dag.khinkal.molapi.http.retrofit.util

import dag.khinkal.molapi.http.model.Headers
import dag.khinkal.molapi.http.model.HttpBody
import dag.khinkal.molapi.http.model.HttpMethod
import dag.khinkal.molapi.http.model.HttpRequest
import dag.khinkal.molapi.http.model.HttpUrl
import dag.khinkal.molapi.http.model.JsonBody
import okhttp3.Request
import okhttp3.RequestBody
import okio.Buffer

public fun Request.toMolApiHttpRequestOrNull(): HttpRequest? {
    val method = method.toMolApiMethodOrNull() ?: return null
    return HttpRequest(
        url = HttpUrl(
            scheme = url.scheme,
            host = url.host,
            port = url.port,
            path = url.encodedPath,
            queryParameters = url.queryParameterNames.associateWith { name ->
                url.queryParameterValues(name)
                    .map { value -> value.orEmpty() }
            },
        ),
        headers = headers.toMolApiHeaders(),
        body = body.toMolApiBody(),
        method = method,
    )
}

private fun String.toMolApiMethodOrNull(): HttpMethod? = when (uppercase()) {
    "GET" -> HttpMethod.GET
    "POST" -> HttpMethod.POST
    "PUT" -> HttpMethod.PUT
    "PATCH" -> HttpMethod.PATCH
    "DELETE" -> HttpMethod.DELETE
    else -> null
}

private fun okhttp3.Headers.toMolApiHeaders(): Headers = Headers(
    names().associateWith { name ->
        values(name).toSet()
    },
)

private fun RequestBody?.toMolApiBody(): HttpBody {
    if (this == null) {
        return JsonBody("")
    }

    val buffer = Buffer()
    writeTo(buffer)
    return JsonBody(buffer.readUtf8())
}
