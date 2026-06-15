package dag.khinkal.molapi.http.retrofit

import dag.khinkal.molapi.http.model.Headers
import dag.khinkal.molapi.http.model.HttpResponse
import dag.khinkal.molapi.http.model.JsonBody
import dag.khinkal.molapi.http.retrofit.util.toOkHttpResponse
import okhttp3.Request
import kotlin.test.Test
import kotlin.test.assertEquals

class OkHttpResponseAdapterTest {

    @Test
    fun mapsMolApiResponseToOkHttpResponse() {
        val request = Request.Builder()
            .url("https://some.com/tasks")
            .build()
        val response = HttpResponse(
            headers = Headers(
                mapOf(
                    "Content-Type" to setOf("application/json"),
                    "X-Test" to setOf("one", "two"),
                ),
            ),
            body = JsonBody("""{"tasks":[]}"""),
            statusCode = 201,
        )

        response.toOkHttpResponse(request).use { okHttpResponse ->
            assertEquals(201, okHttpResponse.code)
            assertEquals("""{"tasks":[]}""", okHttpResponse.body.string())
            assertEquals("application/json", okHttpResponse.header("Content-Type"))
            assertEquals(listOf("one", "two"), okHttpResponse.headers.values("X-Test"))
        }
    }
}
