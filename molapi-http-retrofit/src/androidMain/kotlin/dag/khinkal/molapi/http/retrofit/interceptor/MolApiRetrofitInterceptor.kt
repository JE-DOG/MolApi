package dag.khinkal.molapi.http.retrofit.interceptor

import dag.khinkal.molapi.core.matcher.ApiRequestMatcher
import dag.khinkal.molapi.core.registry.ReadApiMockRegistry
import dag.khinkal.molapi.http.model.HttpRequest
import dag.khinkal.molapi.http.model.HttpResponse
import dag.khinkal.molapi.http.retrofit.util.toMolApiHttpRequestOrNull
import dag.khinkal.molapi.http.retrofit.util.toOkHttpResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

public class MolApiRetrofitInterceptor(
    private val registry: ReadApiMockRegistry<
            HttpRequest,
            ApiRequestMatcher<HttpRequest>,
            HttpResponse
            >,
) : Interceptor {

    public override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val molApiRequest = request.toMolApiHttpRequestOrNull()
        val mock = molApiRequest?.let(registry::find)
            ?: return chain.proceed(request)

        return mock.response.toOkHttpResponse(request)
    }
}

public fun OkHttpClient.Builder.addMolApiInterceptor(
    registry: ReadApiMockRegistry<
            HttpRequest,
            ApiRequestMatcher<HttpRequest>,
            HttpResponse
            >,
): OkHttpClient.Builder = addInterceptor(MolApiRetrofitInterceptor(registry))
