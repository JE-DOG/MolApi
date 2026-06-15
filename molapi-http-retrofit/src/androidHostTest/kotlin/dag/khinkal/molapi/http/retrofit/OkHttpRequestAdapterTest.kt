package dag.khinkal.molapi.http.retrofit

import dag.khinkal.molapi.http.model.HttpMethod
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
            .url("https://some.com/tasks")
            .header("Accept", "application/json")
            .post("""{"title":"task"}""".toRequestBody("application/json".toMediaType()))
            .build()

        val molApiRequest = request.toMolApiHttpRequestOrNull()

        assertEquals("https://some.com/tasks", molApiRequest?.url)
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
