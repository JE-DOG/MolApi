package dag.khinkal.molapi.http.retrofit

import dag.khinkal.molapi.http.gson.util.GsonHttpResponse
import dag.khinkal.molapi.http.matcher.BaseHttpRequestMatcher
import dag.khinkal.molapi.http.model.BaseHttpApiMock
import dag.khinkal.molapi.http.model.Headers
import dag.khinkal.molapi.http.model.HttpMethod
import dag.khinkal.molapi.http.model.JsonBody
import dag.khinkal.molapi.http.retrofit.interceptor.addMolApiInterceptor
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import kotlin.test.Test
import kotlin.test.assertEquals

class MolApiRetrofitInterceptorTest {

    @Test
    fun returnsMockResponseWhenRegistryFindsMatch() {
        val registry = dag.khinkal.molapi.http.registry.HttpApiMockRegistry().apply {
            add(
                BaseHttpApiMock(
                    url = "task_mock",
                    matcher = BaseHttpRequestMatcher(
                        urlRegex = "https://some.com/tasks/42",
                        method = HttpMethod.GET,
                    ),
                    response = GsonHttpResponse(
                        headers = Headers(
                            mapOf("Content-Type" to setOf("application/json")),
                        ),
                        body = TestTask(id = 42, title = "from molapi"),
                    ),
                ),
            )
        }
        val service = createService(
            baseUrl = "https://some.com/",
            client = OkHttpClient.Builder()
                .addMolApiInterceptor(registry)
                .build(),
        )

        val task = service.fetchTask().execute().body()

        assertEquals(TestTask(id = 42, title = "from molapi"), task)
    }

    @Test
    fun proceedsWithRealRequestWhenRegistryDoesNotFindMatch() {
        MockWebServer().use { server ->
            server.start()
            server.enqueue(MockResponse(code = 202, body = """{"id":7,"title":"real"}"""))
            val service = createService(
                baseUrl = server.url("/").toString(),
                client = OkHttpClient.Builder()
                    .addMolApiInterceptor(dag.khinkal.molapi.http.registry.HttpApiMockRegistry())
                    .build(),
            )

            val response = service.fetchTask().execute()

            assertEquals(202, response.code())
            assertEquals(TestTask(id = 7, title = "real"), response.body())
            assertEquals(1, server.requestCount)
        }
    }

    @Test
    fun matchesRequestBodyWhenRetrofitRequestHasJsonBody() {
        val registry = dag.khinkal.molapi.http.registry.HttpApiMockRegistry().apply {
            add(
                BaseHttpApiMock(
                    url = "create_task_mock",
                    matcher = BaseHttpRequestMatcher(
                        urlRegex = "https://some.com/tasks",
                        method = HttpMethod.POST,
                        body = JsonBody("""{"title":"new"}"""),
                    ),
                    response = GsonHttpResponse(
                        headers = Headers(
                            mapOf("Content-Type" to setOf("application/json")),
                        ),
                        body = TestTask(id = 9, title = "new"),
                    ),
                ),
            )
        }
        val service = createService(
            baseUrl = "https://some.com/",
            client = OkHttpClient.Builder()
                .addMolApiInterceptor(registry)
                .build(),
        )

        val task = service.createTask(CreateTaskRequest(title = "new")).execute().body()

        assertEquals(TestTask(id = 9, title = "new"), task)
    }

    private fun createService(
        baseUrl: String,
        client: OkHttpClient,
    ): TestService = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(TestService::class.java)

    private interface TestService {

        @GET("tasks/42")
        fun fetchTask(): Call<TestTask>

        @POST("tasks")
        fun createTask(@Body body: CreateTaskRequest): Call<TestTask>
    }

    private data class CreateTaskRequest(
        val title: String,
    )

    private data class TestTask(
        val id: Int,
        val title: String,
    )
}
