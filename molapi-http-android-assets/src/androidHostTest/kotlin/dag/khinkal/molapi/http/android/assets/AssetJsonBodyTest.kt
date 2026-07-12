package dag.khinkal.molapi.http.android.assets

import dag.khinkal.molapi.http.assets.AssetJsonBody
import dag.khinkal.molapi.http.model.HttpMethod
import dag.khinkal.molapi.http.model.HttpRequest
import dag.khinkal.molapi.http.model.HttpResponse
import dag.khinkal.molapi.http.model.HttpUrl
import dag.khinkal.molapi.http.model.JsonBody
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AssetJsonBodyTest {

    @Test
    fun createsBodyFromContextAsset() {
        val context = RuntimeEnvironment.getApplication()

        val body = AssetJsonBody(
            context = context,
            path = "responses/task.json",
        )

        assertEquals(JsonBody("{\n  \"id\": 42,\n  \"title\": \"from assets\"\n}"), body)
    }

    @Test
    fun createsBodyFromAssetManagerForRequestAndResponseBody() {
        val context = RuntimeEnvironment.getApplication()
        val requestBody = AssetJsonBody(
            assetManager = context.assets,
            path = "requests/create-task.json",
        )
        val responseBody = AssetJsonBody(
            assetManager = context.assets,
            path = "responses/task.json",
        )

        val request = HttpRequest(
            url = HttpUrl(
                scheme = "https",
                host = "some.com",
                port = 443,
                path = "/tasks",
            ),
            method = HttpMethod.POST,
            body = requestBody,
        )
        val response = HttpResponse(
            body = responseBody,
            statusCode = 201,
        )

        assertEquals(JsonBody("{\n  \"title\": \"from request assets\"\n}"), request.body)
        assertEquals(JsonBody("{\n  \"id\": 42,\n  \"title\": \"from assets\"\n}"), response.body)
        assertEquals(201, response.statusCode)
    }
}
