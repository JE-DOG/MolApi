package dag.khinkal.molapi.http.ktor.util

import dag.khinkal.molapi.http.model.Headers
import dag.khinkal.molapi.http.model.HttpBody
import dag.khinkal.molapi.http.model.HttpMethod
import dag.khinkal.molapi.http.model.HttpRequest
import dag.khinkal.molapi.http.model.JsonBody
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.content.OutgoingContent
import io.ktor.http.content.TextContent
import io.ktor.http.Headers as KtorHeaders
import io.ktor.http.HttpMethod as KtorHttpMethod

public fun HttpRequestBuilder.toMolApiHttpRequestOrNull(): HttpRequest? {
    val method = method.toMolApiMethodOrNull() ?: return null
    return HttpRequest(
        url = url.buildString(),
        headers = headers.build().toMolApiHeaders(),
        body = body.toMolApiBody(),
        method = method
    )
}

private fun KtorHttpMethod.toMolApiMethodOrNull(): HttpMethod? = when (this) {
    KtorHttpMethod.Get -> HttpMethod.GET
    KtorHttpMethod.Post -> HttpMethod.POST
    KtorHttpMethod.Put -> HttpMethod.PUT
    KtorHttpMethod.Patch -> HttpMethod.PATCH
    KtorHttpMethod.Delete -> HttpMethod.DELETE
    else -> null
}

private fun KtorHeaders.toMolApiHeaders(): Headers = Headers(
    entries().associate { (name, values) ->
        name to values.toSet()
    }
)

private fun Any.toMolApiBody(): HttpBody = when (this) {
    is String -> JsonBody(this)
    is TextContent -> JsonBody(text)
    is OutgoingContent.NoContent -> JsonBody("")
    else -> JsonBody(toString())
}
