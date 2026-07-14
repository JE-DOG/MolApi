package dag.khinkal.molapi.http.editor

import dag.khinkal.molapi.http.matcher.RawHttpUrlRequestMatcher
import dag.khinkal.molapi.http.model.Headers
import dag.khinkal.molapi.http.model.HttpMethod
import dag.khinkal.molapi.http.model.HttpResponse
import dag.khinkal.molapi.http.model.JsonBody
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class HttpRoomApiMockParserTest {

    @Test
    fun encodesAndDecodesRawHttpUrlMatcher() {
        val matcher = RawHttpUrlRequestMatcher(
            rawUrl = "/todos",
            method = HttpMethod.POST,
            body = JsonBody("""{"id":1}"""),
            headers = Headers(mapOf("Authorization" to listOf("Bearer token"))),
        )

        val encoded = HttpRoomApiMockParser.encodeMatcher(matcher)
        val decoded = assertIs<RawHttpUrlRequestMatcher>(
            HttpRoomApiMockParser.decodeMatcher(encoded),
        )

        assertEquals(matcher.rawUrl, decoded.rawUrl)
        assertEquals(matcher.method, decoded.method)
        assertEquals(matcher.body, decoded.body)
        assertEquals(matcher.headers, decoded.headers)
    }

    @Test
    fun encodesAndDecodesHttpResponse() {
        val response = HttpResponse(
            body = JsonBody("""{"status":"ok"}"""),
            headers = Headers(mapOf("Content-Type" to listOf("application/json"))),
            statusCode = 201,
        )

        val encoded = HttpRoomApiMockParser.encodeResponse(response)
        val decoded = HttpRoomApiMockParser.decodeResponse(encoded)

        assertEquals(response, decoded)
    }
}
