package dag.khinkal.molapi.http.editor

import dag.khinkal.molapi.core.matcher.ApiRequestMatcher
import dag.khinkal.molapi.http.matcher.BaseHttpRequestMatcher
import dag.khinkal.molapi.http.matcher.RawHttpUrlRequestMatcher
import dag.khinkal.molapi.http.model.Headers
import dag.khinkal.molapi.http.model.HttpBody
import dag.khinkal.molapi.http.model.HttpMethod
import dag.khinkal.molapi.http.model.HttpRequest
import dag.khinkal.molapi.http.model.HttpResponse
import dag.khinkal.molapi.http.model.HttpUrl
import dag.khinkal.molapi.http.model.JsonBody
import dag.khinkal.molapi.room.ApiMockParser
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

internal object HttpRoomApiMockParser : ApiMockParser<
        HttpRequest,
        ApiRequestMatcher<HttpRequest>,
        HttpResponse,
        > {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    override fun encodeMatcher(matcher: ApiRequestMatcher<HttpRequest>): String {
        val value = when (matcher) {
            is BaseHttpRequestMatcher -> RoomHttpMatcher(
                httpUrl = matcher.url?.toRoomHttpUrl(),
                method = matcher.method?.name,
                body = matcher.body.asJsonBodyText(),
                headers = matcher.headers?.values,
            )

            is RawHttpUrlRequestMatcher -> RoomHttpMatcher(
                rawUrl = matcher.rawUrl,
                method = matcher.method?.name,
                body = matcher.body.asJsonBodyText(),
                headers = matcher.headers?.values,
            )

            else -> error(
                "Room HTTP mock registry supports BaseHttpRequestMatcher and " +
                        "RawHttpUrlRequestMatcher only",
            )
        }

        return json.encodeToString(value)
    }

    override fun decodeMatcher(matcher: String): ApiRequestMatcher<HttpRequest> {
        val value = json.decodeFromString<RoomHttpMatcher>(matcher)
        val rawUrl = value.rawUrl

        return if (!rawUrl.isNullOrBlank()) {
            RawHttpUrlRequestMatcher(
                rawUrl = rawUrl,
                method = value.method?.let(HttpMethod::valueOf),
                body = value.body?.let(::JsonBody),
                headers = value.headers?.let(::Headers),
            )
        } else {
            BaseHttpRequestMatcher(
                url = value.httpUrl?.toHttpUrl(),
                method = value.method?.let(HttpMethod::valueOf),
                body = value.body?.let(::JsonBody),
                headers = value.headers?.let(::Headers),
            )
        }
    }

    override fun encodeResponse(response: HttpResponse): String =
        json.encodeToString(
            RoomHttpResponse(
                body = response.body.asJsonBodyText(),
                headers = response.headers?.values,
                statusCode = response.statusCode,
            ),
        )

    override fun decodeResponse(response: String): HttpResponse {
        val value = json.decodeFromString<RoomHttpResponse>(response)
        return HttpResponse(
            body = value.body?.let(::JsonBody),
            headers = value.headers?.let(::Headers),
            statusCode = value.statusCode,
        )
    }
}

@Serializable
private data class RoomHttpMatcher(
    val httpUrl: RoomHttpUrl? = null,
    val rawUrl: String? = null,
    val method: String?,
    val body: String?,
    val headers: Map<String, List<String>>? = null,
)

@Serializable
private data class RoomHttpUrl(
    val scheme: String? = null,
    val host: String? = null,
    val port: Int? = null,
    val path: String? = null,
    val queryParameters: Map<String, List<String>> = emptyMap(),
)

@Serializable
private data class RoomHttpResponse(
    val body: String?,
    val headers: Map<String, List<String>>?,
    val statusCode: Int,
)

private fun HttpBody?.asJsonBodyText(): String? = when (this) {
    null -> null
    is JsonBody -> body
    else -> error("Room HTTP mock registry supports JsonBody only")
}

private fun HttpUrl.toRoomHttpUrl(): RoomHttpUrl = RoomHttpUrl(
    scheme = scheme,
    host = host,
    port = port,
    path = path,
    queryParameters = queryParameters,
)

private fun RoomHttpUrl.toHttpUrl(): HttpUrl = HttpUrl(
    scheme = scheme,
    host = host,
    port = port,
    path = path,
    queryParameters = queryParameters,
)
