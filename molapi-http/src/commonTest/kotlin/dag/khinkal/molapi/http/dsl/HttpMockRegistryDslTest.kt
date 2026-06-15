package dag.khinkal.molapi.http.dsl

import dag.khinkal.molapi.core.matcher.ApiRequestMatcher
import dag.khinkal.molapi.core.registry.impl.InMemoryApiMockRegistry
import dag.khinkal.molapi.http.model.Headers
import dag.khinkal.molapi.http.model.HttpBody
import dag.khinkal.molapi.http.model.HttpMethod
import dag.khinkal.molapi.http.model.HttpRequest
import dag.khinkal.molapi.http.model.HttpResponse
import dag.khinkal.molapi.http.model.JsonBody
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class HttpMockRegistryDslTest {

    @Test
    fun registersMocksForEveryHttpMethod() {
        val registry = testRegistry()

        registry.get("/tasks") { HttpResponse(statusCode = 200) }
        registry.post("/tasks") { HttpResponse(statusCode = 201) }
        registry.put("/tasks/1") { HttpResponse(statusCode = 202) }
        registry.patch("/tasks/1") { HttpResponse(statusCode = 203) }
        registry.head("/tasks/1") { HttpResponse(statusCode = 204) }
        registry.delete("/tasks/1") { HttpResponse(statusCode = 205) }

        assertEquals(200, registry.responseFor(HttpMethod.GET, "/tasks").statusCode)
        assertEquals(201, registry.responseFor(HttpMethod.POST, "/tasks").statusCode)
        assertEquals(202, registry.responseFor(HttpMethod.PUT, "/tasks/1").statusCode)
        assertEquals(203, registry.responseFor(HttpMethod.PATCH, "/tasks/1").statusCode)
        assertEquals(204, registry.responseFor(HttpMethod.HEAD, "/tasks/1").statusCode)
        assertEquals(205, registry.responseFor(HttpMethod.DELETE, "/tasks/1").statusCode)
    }

    @Test
    fun matchesOptionalRequestHeadersAndBody() {
        val registry = testRegistry()
        val headers = Headers(mapOf("Authorization" to setOf("Bearer token")))
        val body = JsonBody("""{"title":"new"}""")

        registry.post(
            url = "/tasks",
            headers = headers,
            body = body,
        ) {
            HttpResponse(statusCode = 201)
        }

        assertEquals(
            201,
            registry.responseFor(
                method = HttpMethod.POST,
                url = "/tasks",
                headers = headers,
                body = body,
            ).statusCode,
        )
    }

    @Test
    fun registersGenericHttpMock() {
        val registry = testRegistry()

        registry.http(
            method = HttpMethod.POST,
            url = "/tasks",
            response = HttpResponse(statusCode = 201),
        )

        assertEquals(201, registry.responseFor(HttpMethod.POST, "/tasks").statusCode)
    }

    @Test
    fun registersDirectHttpResponse() {
        val registry = testRegistry()
        val response = HttpResponse(
            body = JsonBody("""{"accepted":true}"""),
            headers = Headers.jsonContent(),
            statusCode = 202,
        )

        registry.patch("/tasks/1", response = response)

        assertEquals(response, registry.responseFor(HttpMethod.PATCH, "/tasks/1"))
    }

    @Test
    fun registersHttpBodyResponse() {
        val registry = testRegistry()

        registry.get("/tasks", response = JsonBody("""{"tasks":[]}"""))

        assertEquals(
            JsonBody("""{"tasks":[]}"""),
            registry.responseFor(HttpMethod.GET, "/tasks").body,
        )
    }

    @Test
    fun registersJsonStringResponse() {
        val registry = testRegistry()

        registry.get("/tasks", json = """{"tasks":[]}""")

        assertEquals(
            JsonBody("""{"tasks":[]}"""),
            registry.responseFor(HttpMethod.GET, "/tasks").body,
        )
    }

    @Test
    fun keepsHttpResponseHeadersAndStatusForBodyShortcut() {
        val registry = testRegistry()
        val headers = Headers.jsonContent()

        registry.get(
            url = "/tasks",
            response = JsonBody("""{"tasks":[]}"""),
            responseHeaders = headers,
            statusCode = 206,
        )

        val response = registry.responseFor(HttpMethod.GET, "/tasks")

        assertEquals(JsonBody("""{"tasks":[]}"""), response.body)
        assertEquals(headers, response.headers)
        assertEquals(206, response.statusCode)
    }

    private fun testRegistry(): InMemoryApiMockRegistry<
            HttpRequest,
            ApiRequestMatcher<HttpRequest>,
            HttpResponse,
            > = InMemoryApiMockRegistry()

    private fun InMemoryApiMockRegistry<
            HttpRequest,
            ApiRequestMatcher<HttpRequest>,
            HttpResponse,
            >.responseFor(
        method: HttpMethod,
        url: String,
        headers: Headers? = null,
        body: HttpBody? = null,
    ): HttpResponse {
        val response = find(
            HttpRequest(
                url = url,
                method = method,
                headers = headers,
                body = body,
            ),
        )?.response

        return assertNotNull(response)
    }
}
