package dag.khinkal.molapi

import dag.khinkal.molapi.core.matcher.ApiRequestMatcher
import dag.khinkal.molapi.core.model.ApiMock
import dag.khinkal.molapi.http.matcher.BaseHttpRequestMatcher
import dag.khinkal.molapi.http.model.BaseHttpApiMock
import dag.khinkal.molapi.http.model.Headers
import dag.khinkal.molapi.http.model.HttpBody
import dag.khinkal.molapi.http.model.HttpMethod
import dag.khinkal.molapi.http.model.HttpRequest
import dag.khinkal.molapi.http.model.HttpResponse
import dag.khinkal.molapi.http.model.JsonBody
import dag.khinkal.molapi.http.registry.HttpApiMockRegistry
import dag.khinkal.molapi.room.ApiMockParser
import dag.khinkal.molapi.room.MolApiRoomDatabase
import dag.khinkal.molapi.room.RoomApiMockRecord
import dag.khinkal.molapi.room.RoomApiMockRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.Serializable

internal fun createRoomHttpMockRegistry(
    database: MolApiRoomDatabase,
    coroutineScope: CoroutineScope,
): HttpApiMockRegistry =
    RoomApiMockRegistry(
        database = database,
        parser = HttpRoomApiMockParser,
        coroutineScope = coroutineScope,
    )

private object HttpRoomApiMockParser : ApiMockParser<
        HttpRequest,
        ApiRequestMatcher<HttpRequest>,
        HttpResponse,
        > {

    override fun encodeMatcher(matcher: ApiRequestMatcher<HttpRequest>): String {
        val baseMatcher = requireNotNull(matcher as? BaseHttpRequestMatcher) {
            "Room HTTP mock registry supports BaseHttpRequestMatcher only"
        }
        return AppJson.encodeToString(
            RoomHttpMatcher(
                url = baseMatcher.url,
                method = baseMatcher.method?.name,
                body = baseMatcher.body.asJsonBodyText(),
            ),
        )
    }

    override fun decodeMatcher(matcher: String): ApiRequestMatcher<HttpRequest> {
        val value = AppJson.decodeFromString<RoomHttpMatcher>(matcher)
        return BaseHttpRequestMatcher(
            url = value.url,
            method = value.method?.let(HttpMethod::valueOf),
            body = value.body?.let(::JsonBody),
        )
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
    ): ApiMock<HttpRequest, ApiRequestMatcher<HttpRequest>, HttpResponse> {
        val matcher = decodeMatcher(record.matcher)

        return BaseHttpApiMock(
            matcher = matcher,
            response = decodeResponse(record.response),
        )
    }
}

@Serializable
private data class RoomHttpMatcher(
    val url: String?,
    val method: String?,
    val body: String?,
)

@Serializable
private data class RoomHttpResponse(
    val body: String?,
    val headers: Map<String, Set<String>>?,
    val statusCode: Int,
)

private fun HttpBody?.asJsonBodyText(): String? = when (this) {
    null -> null
    is JsonBody -> body
    else -> error("Room HTTP mock registry supports JsonBody only")
}
