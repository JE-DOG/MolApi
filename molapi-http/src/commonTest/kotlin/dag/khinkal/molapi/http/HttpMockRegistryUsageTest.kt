package dag.khinkal.molapi.http

import dag.khinkal.molapi.core.registry.add
import dag.khinkal.molapi.http.matcher.BaseHttpRequestMatcher
import dag.khinkal.molapi.http.model.BaseHttpApiMock
import dag.khinkal.molapi.http.model.Headers
import dag.khinkal.molapi.http.model.HttpMethod
import dag.khinkal.molapi.http.model.HttpRequest
import dag.khinkal.molapi.http.model.HttpResponse
import dag.khinkal.molapi.http.model.HttpUrl
import dag.khinkal.molapi.http.model.JsonBody
import dag.khinkal.molapi.http.registry.HttpInMemoryApiMockRegistry
import kotlin.test.Test
import kotlin.test.assertEquals

class HttpMockRegistryUsageTest {

    @Test
    fun baseHttpApiMockGeneratesIdFromMatcherAndResponse() {
        val matcher = BaseHttpRequestMatcher(
            url = HttpUrl(
                scheme = "https",
                host = "some.com",
                path = "/tasks",
            ),
        )
        val response = HttpResponse()

        val mock = BaseHttpApiMock(
            matcher = matcher,
            response = response,
        )

        assertEquals(BaseHttpApiMock(matcher = matcher, response = response).id, mock.id)
    }

    @Test
    fun findsRegisteredHttpMock() {
        val request = HttpRequest(
            url = HttpUrl(
                scheme = "https",
                host = "some.com",
                port = 443,
                path = "/tasks",
            ),
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
            matcher = BaseHttpRequestMatcher(
                url = HttpUrl(path = "/tasks"),
                method = HttpMethod.GET,
            ),
            response = response,
        )

        val registry = HttpInMemoryApiMockRegistry()

        registry.add(mock)

        val foundMock = registry.find(request)

        assertEquals(response, foundMock?.response)
    }
}
