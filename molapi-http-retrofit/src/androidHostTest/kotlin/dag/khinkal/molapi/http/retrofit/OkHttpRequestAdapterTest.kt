package dag.khinkal.molapi.http.retrofit

import dag.khinkal.molapi.http.model.HttpMethod
import dag.khinkal.molapi.http.model.HttpUrl
import dag.khinkal.molapi.http.model.JsonBody
import dag.khinkal.molapi.http.retrofit.util.toMolApiHttpRequestOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.test.Test
import kotlin.test.assertEquals

class OkHttpRequestAdapterTest {

    @Test
    fun mapsSupportedOkHttpRequestToMolApiRequest() {
        val request = Request.Builder()
            .url("https://some.com:8443/tasks?tag=one&tag=one&tag=two&page=2")
            .header("Accept", "application/json")
            .post("""{"title":"task"}""".toRequestBody("application/json".toMediaType()))
            .build()

        val molApiRequest = request.toMolApiHttpRequestOrNull()

        assertEquals(
            HttpUrl(
                scheme = "https",
                host = "some.com",
                port = 8443,
                path = "/tasks",
                queryParameters = mapOf(
                    "tag" to listOf("one", "one", "two"),
                    "page" to listOf("2"),
                ),
            ),
            molApiRequest?.url,
        )
        assertEquals(HttpMethod.POST, molApiRequest?.method)
        assertEquals(JsonBody("""{"title":"task"}"""), molApiRequest?.body)
        assertEquals(setOf("application/json"), molApiRequest?.headers?.values?.get("Accept"))
    }

    @Test
    fun returnsNullForUnsupportedOkHttpMethod() {
        val request = Request.Builder()
            .url("https://some.com/tasks")
            .method("OPTIONS", null)
            .build()

        assertEquals(null, request.toMolApiHttpRequestOrNull())
    }
}
