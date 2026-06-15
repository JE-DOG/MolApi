package dag.khinkal.molapi.http.ktor

import dag.khinkal.molapi.http.ktor.util.toMolApiHttpRequestOrNull
import dag.khinkal.molapi.http.model.HttpMethod
import dag.khinkal.molapi.http.model.JsonBody
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.headers
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlin.test.Test
import kotlin.test.assertEquals

class KtorHttpRequestAdapterTest {

    @Test
    fun mapsSupportedKtorRequestToMolApiRequest() {
        val builder = HttpRequestBuilder().apply {
            url("https://some.com/tasks")
            method = io.ktor.http.HttpMethod.Post
            contentType(ContentType.Application.Json)
            headers {
                append(HttpHeaders.Accept, "application/json")
            }
            setBody("{}")
        }

        val request = builder.toMolApiHttpRequestOrNull()

        assertEquals("https://some.com/tasks", request?.url)
        assertEquals(HttpMethod.POST, request?.method)
        assertEquals(JsonBody("{}"), request?.body)
        assertEquals(setOf("application/json"), request?.headers?.values?.get(HttpHeaders.Accept))
    }

    @Test
    fun returnsNullForUnsupportedKtorMethod() {
        val builder = HttpRequestBuilder().apply {
            url("https://some.com/tasks")
            method = io.ktor.http.HttpMethod.Options
        }

        assertEquals(null, builder.toMolApiHttpRequestOrNull())
    }
}
