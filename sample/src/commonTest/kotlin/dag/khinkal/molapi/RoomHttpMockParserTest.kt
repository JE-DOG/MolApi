package dag.khinkal.molapi

import dag.khinkal.molapi.http.matcher.BaseHttpRequestMatcher
import dag.khinkal.molapi.http.matcher.RawHttpUrlRequestMatcher
import dag.khinkal.molapi.http.model.HttpMethod
import dag.khinkal.molapi.http.model.HttpUrl
import kotlin.test.Test
import kotlin.test.assertEquals

class RoomHttpMockParserTest {

    @Test
    fun preservesRawHttpUrlRequestMatcher() {
        val matcher = RawHttpUrlRequestMatcher(
            rawUrl = "example.com/tasks",
            method = HttpMethod.GET,
        )

        val decoded = HttpRoomApiMockParser.decodeMatcher(
            HttpRoomApiMockParser.encodeMatcher(matcher),
        ) as RawHttpUrlRequestMatcher

        assertEquals("example.com/tasks", decoded.rawUrl)
        assertEquals(HttpMethod.GET, decoded.method)
    }

    @Test
    fun roundTripsStructuredHttpUrl() {
        val matcher = BaseHttpRequestMatcher(
            url = HttpUrl(
                scheme = "https",
                host = "some.com",
                port = 443,
                path = "/tasks",
                queryParameters = mapOf("state" to listOf("active", "active", "owned")),
            ),
            method = HttpMethod.GET,
        )

        val encoded = HttpRoomApiMockParser.encodeMatcher(matcher)
        val decoded = HttpRoomApiMockParser.decodeMatcher(encoded) as BaseHttpRequestMatcher

        assertEquals(matcher.url, decoded.url)
        assertEquals(matcher.method, decoded.method)
    }

    @Test
    fun decodesLegacyRelativeUrlWithQuery() {
        val decoded = HttpRoomApiMockParser.decodeMatcher(
            """{"url":"/todos?state=active&state=active&state=owned","method":"GET","body":null}""",
        ) as BaseHttpRequestMatcher

        assertEquals(
            HttpUrl(
                path = "/todos",
                queryParameters = mapOf("state" to listOf("active", "active", "owned")),
            ),
            decoded.url,
        )
        assertEquals(HttpMethod.GET, decoded.method)
    }

    @Test
    fun decodesLegacyAbsoluteUrl() {
        val decoded = HttpRoomApiMockParser.decodeMatcher(
            """{"url":"https://some.com/tasks?page=2","method":"GET","body":null}""",
        ) as BaseHttpRequestMatcher

        assertEquals(
            HttpUrl(
                scheme = "https",
                host = "some.com",
                port = 443,
                path = "/tasks",
                queryParameters = mapOf("page" to listOf("2")),
            ),
            decoded.url,
        )
    }
}
