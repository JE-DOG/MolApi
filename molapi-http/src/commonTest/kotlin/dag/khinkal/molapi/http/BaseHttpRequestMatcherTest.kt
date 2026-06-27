package dag.khinkal.molapi.http

import dag.khinkal.molapi.http.matcher.BaseHttpRequestMatcher
import dag.khinkal.molapi.http.model.Headers
import dag.khinkal.molapi.http.model.HttpBody
import dag.khinkal.molapi.http.model.HttpMethod
import dag.khinkal.molapi.http.model.HttpRequest
import dag.khinkal.molapi.http.model.JsonBody
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BaseHttpRequestMatcherTest {

    @Test
    fun matchesByUrl() {
        val matcher = BaseHttpRequestMatcher(url = "https://some.com/tasks")

        assertTrue(matcher.matches(testRequest(url = "https://some.com/tasks")))
        assertFalse(matcher.matches(testRequest(url = "https://some.com/users")))
    }

    @Test
    fun matchesByUrlPart() {
        val matcher = BaseHttpRequestMatcher(url = "/tasks/")

        assertTrue(matcher.matches(testRequest(url = "https://some.com/tasks/42")))
        assertFalse(matcher.matches(testRequest(url = "https://some.com/users/42")))
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
                    url = "https://other.com/tasks",
                    body = JsonBody("""{"tasks":[]}"""),
                    method = HttpMethod.DELETE
                )
            )
        )
    }

    private fun testRequest(
        url: String = "https://some.com/tasks",
        headers: Headers? = Headers.empty(),
        body: HttpBody = JsonBody("{}"),
        method: HttpMethod = HttpMethod.GET
    ): HttpRequest = HttpRequest(
        url = url,
        headers = headers,
        body = body,
        method = method
    )
}
