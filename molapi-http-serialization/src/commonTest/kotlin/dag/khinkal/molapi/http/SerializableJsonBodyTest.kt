package dag.khinkal.molapi.http

import dag.khinkal.molapi.http.model.Headers
import dag.khinkal.molapi.http.model.JsonBody
import dag.khinkal.molapi.http.serializable.util.JsonHttpResponse
import dag.khinkal.molapi.http.serializable.util.SerializableJsonBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class SerializableJsonBodyTest {

    @Test
    fun createsJsonBodyFromSerializableValueUsingDefaultJson() {
        val body = SerializableJsonBody(TestTask(id = 42, taskTitle = "from serialization"))

        assertEquals(JsonBody("""{"id":42,"taskTitle":"from serialization"}"""), body)
    }

    @Test
    fun createsJsonBodyFromSerializableValueUsingConfiguredJson() {
        val body = SerializableJsonBody(
            body = TestTask(id = 42, taskTitle = "from serialization"),
            json = Json {
                encodeDefaults = true
            },
        )

        assertEquals(
            JsonBody("""{"id":42,"taskTitle":"from serialization","completed":false}"""),
            body,
        )
    }

    @Test
    fun createsHttpResponseWithSerializedJsonBody() {
        val response = JsonHttpResponse(
            headers = Headers.empty(),
            serializableBody = TestTask(id = 42, taskTitle = "from serialization"),
            statusCode = 201,
        )

        assertEquals(201, response.statusCode)
        assertEquals(Headers.empty(), response.headers)
        assertEquals(JsonBody("""{"id":42,"taskTitle":"from serialization"}"""), response.body)
    }

    @Serializable
    private data class TestTask(
        val id: Int,
        @SerialName("taskTitle")
        val taskTitle: String,
        val completed: Boolean = false,
    )
}
