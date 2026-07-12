package dag.khinkal.molapi.http

import dag.khinkal.molapi.http.matcher.BaseHttpRequestMatcher
import dag.khinkal.molapi.http.model.Headers
import dag.khinkal.molapi.http.model.HttpBody
import dag.khinkal.molapi.http.model.HttpMethod
import dag.khinkal.molapi.http.model.HttpRequest
import dag.khinkal.molapi.http.model.HttpUrl
import dag.khinkal.molapi.http.model.JsonBody
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BaseHttpRequestMatcherTest {

    @Test
    fun matchesByUrlComponents() {
        val matcher = BaseHttpRequestMatcher(
            url = HttpUrl(
                scheme = "HTTPS",
                host = "SOME.COM",
                port = 443,
                path = "/tasks",
            ),
        )

        assertTrue(matcher.matches(testRequest()))
        assertFalse(matcher.matches(testRequest(url = testUrl(host = "other.com"))))
        assertFalse(matcher.matches(testRequest(url = testUrl(port = 8443))))
        assertFalse(matcher.matches(testRequest(url = testUrl(path = "/users"))))
    }

    @Test
    fun matchesByUrlPart() {
        val matcher = BaseHttpRequestMatcher(url = HttpUrl(path = "/tasks/"))

        assertTrue(matcher.matches(testRequest(url = testUrl(path = "/tasks/42"))))
        assertFalse(matcher.matches(testRequest(url = testUrl(path = "/users/42"))))
    }

    @Test
    fun matchesConfiguredQueryParametersWithDuplicatesAndExtraNames() {
        val matcher = BaseHttpRequestMatcher(
            url = HttpUrl(
                queryParameters = mapOf(
                    "filter" to listOf("active", "active", "owned"),
                    "page" to listOf("2"),
                ),
            ),
        )

        assertTrue(
            matcher.matches(
                testRequest(
                    url = testUrl(
                        queryParameters = linkedMapOf(
                            "page" to listOf("2"),
                            "tracking" to listOf("enabled"),
                            "filter" to listOf("active", "active", "owned"),
                        ),
                    ),
                ),
            ),
        )
        assertFalse(
            matcher.matches(
                testRequest(
                    url = testUrl(
                        queryParameters = mapOf(
                            "page" to listOf("2"),
                            "filter" to listOf("active", "owned"),
                        ),
                    ),
                ),
            ),
        )
    }

    @Test
    fun matchesByMethod() {
        val matcher = BaseHttpRequestMatcher(method = HttpMethod.GET)

        assertTrue(matcher.matches(testRequest(method = HttpMethod.GET)))
        assertFalse(matcher.matches(testRequest(method = HttpMethod.POST)))
    }

    @Test
    fun matchesByBody() {
        val matcher = BaseHttpRequestMatcher(body = JsonBody("{}"))

        assertTrue(matcher.matches(testRequest(body = JsonBody("{}"))))
        assertFalse(matcher.matches(testRequest(body = JsonBody("""{"value":1}"""))))
    }

    @Test
    fun matchesAnyRequestWhenAllConditionsAreNull() {
        val matcher = BaseHttpRequestMatcher()

        assertTrue(matcher.matches(testRequest()))
        assertTrue(
            matcher.matches(
                testRequest(
                    url = testUrl(host = "other.com"),
                    body = JsonBody("""{"tasks":[]}"""),
                    method = HttpMethod.DELETE
                )
            )
        )
    }

    private fun testRequest(
        url: HttpUrl = testUrl(),
        headers: Headers? = Headers.empty(),
        body: HttpBody = JsonBody("{}"),
        method: HttpMethod = HttpMethod.GET
    ): HttpRequest = HttpRequest(
        url = url,
        headers = headers,
        body = body,
        method = method
    )

    private companion object {

        fun testUrl(
            scheme: String = "https",
            host: String = "some.com",
            port: Int = 443,
            path: String = "/tasks",
            queryParameters: Map<String, List<String>> = emptyMap(),
        ): HttpUrl = HttpUrl(
            scheme = scheme,
            host = host,
            port = port,
            path = path,
            queryParameters = queryParameters,
        )
    }
}
