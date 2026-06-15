package dag.khinkal.molapi.http.gson

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import dag.khinkal.molapi.http.gson.util.GsonHttpResponse
import dag.khinkal.molapi.http.gson.util.GsonJsonBody
import dag.khinkal.molapi.http.model.Headers
import dag.khinkal.molapi.http.model.JsonBody
import kotlin.test.Test
import kotlin.test.assertEquals

class GsonJsonBodyTest {

    @Test
    fun createsJsonBodyFromValueUsingDefaultGson() {
        val body = GsonJsonBody(TestTask(id = 42, taskTitle = "from gson"))

        assertEquals(JsonBody("""{"id":42,"taskTitle":"from gson"}"""), body)
    }

    @Test
    fun createsJsonBodyFromValueUsingConfiguredGson() {
        val gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()

        val body = GsonJsonBody(TestTask(id = 42, taskTitle = "from gson"), gson)

        assertEquals(JsonBody("""{"id":42,"task_title":"from gson"}"""), body)
    }

    @Test
    fun createsHttpResponseWithSerializedJsonBody() {
        val response = GsonHttpResponse(
            headers = Headers.empty(),
            body = TestTask(id = 42, taskTitle = "from gson"),
            statusCode = 201,
        )

        assertEquals(201, response.statusCode)
        assertEquals(Headers.empty(), response.headers)
        assertEquals(JsonBody("""{"id":42,"taskTitle":"from gson"}"""), response.body)
    }

    private data class TestTask(
        val id: Int,
        val taskTitle: String,
    )
}
