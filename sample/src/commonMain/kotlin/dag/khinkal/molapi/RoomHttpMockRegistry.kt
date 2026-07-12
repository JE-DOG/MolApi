package dag.khinkal.molapi

import dag.khinkal.molapi.core.idgenerator.impl.HashCodeApiMockIdGenerator
import dag.khinkal.molapi.core.matcher.ApiRequestMatcher
import dag.khinkal.molapi.core.model.ApiMock
import dag.khinkal.molapi.http.matcher.BaseHttpRequestMatcher
import dag.khinkal.molapi.http.matcher.RawHttpUrlRequestMatcher
import dag.khinkal.molapi.http.model.BaseHttpApiMock
import dag.khinkal.molapi.http.model.Headers
import dag.khinkal.molapi.http.model.HttpBody
import dag.khinkal.molapi.http.model.HttpMethod
import dag.khinkal.molapi.http.model.HttpRequest
import dag.khinkal.molapi.http.model.HttpResponse
import dag.khinkal.molapi.http.model.HttpUrl
import dag.khinkal.molapi.http.model.JsonBody
import dag.khinkal.molapi.http.registry.HttpApiMockRegistry
import dag.khinkal.molapi.room.ApiMockParser
import dag.khinkal.molapi.room.MolApiRoomDatabase
import dag.khinkal.molapi.room.RoomApiMockRecord
import dag.khinkal.molapi.room.RoomApiMockRegistry
import io.ktor.http.Url
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.Serializable

internal fun createRoomHttpMockRegistry(
    database: MolApiRoomDatabase,
    coroutineScope: CoroutineScope,
): HttpApiMockRegistry =
    RoomApiMockRegistry(
        database = database,
        parser = HttpRoomApiMockParser,
        idGenerator = HashCodeApiMockIdGenerator(),
        coroutineScope = coroutineScope,
    )

internal object HttpRoomApiMockParser : ApiMockParser<
        HttpRequest,
        ApiRequestMatcher<HttpRequest>,
        HttpResponse,
        Any,
        > {

    override fun encodeMatcher(matcher: ApiRequestMatcher<HttpRequest>): String {
        val value = when (matcher) {
            is BaseHttpRequestMatcher -> RoomHttpMatcher(
                httpUrl = matcher.url?.toRoomHttpUrl(),
                method = matcher.method?.name,
                body = matcher.body.asJsonBodyText(),
            )

            is RawHttpUrlRequestMatcher -> RoomHttpMatcher(
                rawUrl = matcher.rawUrl,
                method = matcher.method?.name,
                body = matcher.body.asJsonBodyText(),
            )

            else -> error(
                "Room HTTP mock registry supports BaseHttpRequestMatcher and " +
                        "RawHttpUrlRequestMatcher only",
            )
        }

        return AppJson.encodeToString(value)
    }

    override fun decodeMatcher(matcher: String): ApiRequestMatcher<HttpRequest> {
        val value = AppJson.decodeFromString<RoomHttpMatcher>(matcher)
        val rawUrl = value.rawUrl

        return if (!rawUrl.isNullOrBlank()) {
            RawHttpUrlRequestMatcher(
                rawUrl = rawUrl,
                method = value.method?.let(HttpMethod::valueOf),
                body = value.body?.let(::JsonBody),
            )
        } else {
            BaseHttpRequestMatcher(
                url = value.httpUrl?.toHttpUrl() ?: value.url?.toLegacyHttpUrl(),
                method = value.method?.let(HttpMethod::valueOf),
                body = value.body?.let(::JsonBody),
            )
        }
    }

    override fun encodeResponse(response: HttpResponse): String =
        AppJson.encodeToString(
            RoomHttpResponse(
                body = response.body.asJsonBodyText(),
                headers = response.headers?.values,
                statusCode = response.statusCode,
            ),
        )

    override fun decodeResponse(response: String): HttpResponse {
        val value = AppJson.decodeFromString<RoomHttpResponse>(response)
        return HttpResponse(
            body = value.body?.let(::JsonBody),
            headers = value.headers?.let(::Headers),
            statusCode = value.statusCode,
        )
    }

    override fun decodeRecord(
        record: RoomApiMockRecord,
    ): ApiMock<HttpRequest, ApiRequestMatcher<HttpRequest>, HttpResponse, Any> {
        val matcher = decodeMatcher(record.matcher)

        return BaseHttpApiMock(
            id = record.id.toInt(),
            matcher = matcher,
            response = decodeResponse(record.response),
        )
    }
}

@Serializable
private data class RoomHttpMatcher(
    val url: String? = null,
    val httpUrl: RoomHttpUrl? = null,
    val rawUrl: String? = null,
    val method: String?,
    val body: String?,
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

private fun String.toLegacyHttpUrl(): HttpUrl {
    val parsedUrl = Url(this)
    val hasAuthority = startsWith("http://", ignoreCase = true) ||
            startsWith("https://", ignoreCase = true)

    return HttpUrl(
        scheme = parsedUrl.protocol.name.takeIf { hasAuthority },
        host = parsedUrl.host.takeIf { hasAuthority },
        port = parsedUrl.port.takeIf { hasAuthority },
        path = parsedUrl.encodedPath.ifEmpty { "/" },
        queryParameters = parsedUrl.parameters.entries().associate { (name, values) ->
            name to values.toList()
        },
    )
}
