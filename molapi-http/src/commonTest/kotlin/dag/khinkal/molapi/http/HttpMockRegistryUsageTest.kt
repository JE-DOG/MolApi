package dag.khinkal.molapi.http

import dag.khinkal.molapi.http.matcher.BaseHttpRequestMatcher
import dag.khinkal.molapi.http.model.BaseHttpApiMock
import dag.khinkal.molapi.http.model.Headers
import dag.khinkal.molapi.http.model.HttpMethod
import dag.khinkal.molapi.http.model.HttpRequest
import dag.khinkal.molapi.http.model.HttpResponse
import dag.khinkal.molapi.http.model.JsonBody
import kotlin.test.Test
import kotlin.test.assertEquals

class HttpMockRegistryUsageTest {

    @Test
    fun baseHttpApiMockExposesUrl() {
        val url = "https://some.com/tasks"
        val mock = BaseHttpApiMock(
            url = url,
            matcher = BaseHttpRequestMatcher(urlRegex = url),
            response = HttpResponse(),
        )

        assertEquals(url, mock.url)
    }

    @Test
    fun findsRegisteredHttpMock() {
        val request = HttpRequest(
            url = "https://some.com/tasks",
            headers = Headers.empty(),
            body = JsonBody("{}"),
            method = HttpMethod.GET
        )

        val response = HttpResponse(
            headers = Headers.empty(),
            body = JsonBody("""{"tasks": []}"""),
            statusCode = 200
        )

        val mock = BaseHttpApiMock(
            url = "https://some.com/tasks",
            matcher = BaseHttpRequestMatcher(
                urlRegex = "https://some.com/tasks",
                method = HttpMethod.GET
            ),
            response = response
        )

        val registry = dag.khinkal.molapi.http.registry.HttpApiMockRegistry()

        registry.add(mock)

        val foundMock = registry.find(request)

        assertEquals(response, foundMock?.response)
    }
}
