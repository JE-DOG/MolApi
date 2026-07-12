package dag.khinkal.molapi.http

import dag.khinkal.molapi.http.matcher.RawHttpUrlRequestMatcher
import dag.khinkal.molapi.http.model.HttpMethod
import dag.khinkal.molapi.http.model.HttpRequest
import dag.khinkal.molapi.http.model.HttpUrl
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RawHttpUrlRequestMatcherTest {

    @Test
    fun matchesWhenRequestRawUrlContainsConfiguredText() {
        val matcher = RawHttpUrlRequestMatcher(rawUrl = "example.com/tasks")

        assertTrue(matcher.matches(request(path = "/tasks/42")))
        assertFalse(matcher.matches(request(host = "other.com")))
    }

    @Test
    fun omitsDefaultPortWhenBuildingRawUrl() {
        val matcher = RawHttpUrlRequestMatcher(rawUrl = "https://example.com/tasks")

        assertTrue(matcher.matches(request()))
    }

    @Test
    fun alsoMatchesConfiguredMethod() {
        val matcher = RawHttpUrlRequestMatcher(
            rawUrl = "/tasks",
            method = HttpMethod.POST,
        )

        assertTrue(matcher.matches(request(method = HttpMethod.POST)))
        assertFalse(matcher.matches(request(method = HttpMethod.GET)))
    }

    private fun request(
        host: String = "example.com",
        path: String = "/tasks",
        method: HttpMethod = HttpMethod.GET,
    ): HttpRequest = HttpRequest(
        url = HttpUrl(
            scheme = "https",
            host = host,
            port = 443,
            path = path,
        ),
        method = method,
    )
}
