package dag.khinkal.molapi.http.ktor

import dag.khinkal.molapi.http.ktor.util.toKtorHttpClientCall
import dag.khinkal.molapi.http.model.Headers
import dag.khinkal.molapi.http.model.HttpResponse
import dag.khinkal.molapi.http.model.JsonBody
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import kotlin.test.Test
import kotlin.test.assertEquals

class KtorHttpResponseAdapterTest {

    @Test
    fun mapsMolApiResponseToKtorCall() = kotlinx.coroutines.test.runTest {
        val response = HttpResponse(
            headers = Headers(
                mapOf(
                    HttpHeaders.ContentType to listOf("application/json"),
                    "X-Test" to listOf("one", "two")
                )
            ),
            body = JsonBody("""{"tasks":[]}"""),
            statusCode = 201
        )
        val requestData = HttpRequestBuilder().apply {
            url("https://some.com/tasks")
        }.build()
        val client = HttpClient(MockEngine { respond("") })

        val call = response.toKtorHttpClientCall(
            client = client,
            requestData = requestData,
            callContext = coroutineContext
        )

        assertEquals(201, call.response.status.value)
        assertEquals("""{"tasks":[]}""", call.response.bodyAsText())
        assertEquals("application/json", call.response.headers[HttpHeaders.ContentType])
        assertEquals(listOf("one", "two"), call.response.headers.getAll("X-Test"))

        client.close()
    }

    @Test
    fun mapsNullMolApiResponseBodyToEmptyKtorBody() = kotlinx.coroutines.test.runTest {
        val response = HttpResponse(
            body = null,
            statusCode = 204,
        )
        val requestData = HttpRequestBuilder().apply {
            url("https://some.com/empty")
        }.build()
        val client = HttpClient(MockEngine { respond("") })

        val call = response.toKtorHttpClientCall(
            client = client,
            requestData = requestData,
            callContext = coroutineContext
        )

        assertEquals(204, call.response.status.value)
        assertEquals("", call.response.bodyAsText())

        client.close()
    }
}
