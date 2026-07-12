package dag.khinkal.molapi.http.ktor

import dag.khinkal.molapi.http.ktor.util.toMolApiHttpRequestOrNull
import dag.khinkal.molapi.http.model.HttpMethod
import dag.khinkal.molapi.http.model.HttpUrl
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
import io.ktor.http.HttpMethod as KtorHttpMethod

class KtorHttpRequestAdapterTest {

    @Test
    fun mapsSupportedKtorRequestToMolApiRequest() {
        val builder = HttpRequestBuilder().apply {
            url("https://some.com/tasks?tag=one&tag=one&tag=two&page=2")
            method = KtorHttpMethod.Post
            contentType(ContentType.Application.Json)
            headers {
                append(HttpHeaders.Accept, "application/json")
            }
            setBody("{}")
        }

        val request = builder.toMolApiHttpRequestOrNull()

        assertEquals(
            HttpUrl(
                scheme = "https",
                host = "some.com",
                port = 443,
                path = "/tasks",
                queryParameters = mapOf(
                    "tag" to listOf("one", "one", "two"),
                    "page" to listOf("2"),
                ),
            ),
            request?.url,
        )
        assertEquals(HttpMethod.POST, request?.method)
        assertEquals(JsonBody("{}"), request?.body)
        assertEquals(setOf("application/json"), request?.headers?.values?.get(HttpHeaders.Accept))
    }

    @Test
    fun returnsNullForUnsupportedKtorMethod() {
        val builder = HttpRequestBuilder().apply {
            url("https://some.com/tasks")
            method = KtorHttpMethod.Options
        }

        assertEquals(null, builder.toMolApiHttpRequestOrNull())
    }

    @Test
    fun mapsHeadMethod() {
        val builder = HttpRequestBuilder().apply {
            url("https://some.com/tasks")
            method = KtorHttpMethod.Head
        }

        val request = builder.toMolApiHttpRequestOrNull()

        assertEquals(HttpMethod.HEAD, request?.method)
    }
}
