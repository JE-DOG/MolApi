package dag.khinkal.molapi.http.ktor.util

import dag.khinkal.molapi.http.model.Headers
import dag.khinkal.molapi.http.model.HttpBody
import dag.khinkal.molapi.http.model.HttpResponse
import dag.khinkal.molapi.http.model.JsonBody
import io.ktor.client.HttpClient
import io.ktor.client.call.HttpClientCall
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HeadersBuilder
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.util.date.GMTDate
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.InternalAPI
import kotlin.coroutines.CoroutineContext

@OptIn(InternalAPI::class)
public fun HttpResponse.toKtorHttpClientCall(
    client: HttpClient,
    requestData: HttpRequestData,
    callContext: CoroutineContext
): HttpClientCall = HttpClientCall(
    client = client,
    requestData = requestData,
    responseData = HttpResponseData(
        statusCode = HttpStatusCode.fromValue(statusCode),
        requestTime = GMTDate(),
        headers = headers.toKtorHeaders(),
        version = HttpProtocolVersion.HTTP_1_1,
        body = body.toKtorBody(),
        callContext = callContext,
    )
)

private fun Headers?.toKtorHeaders(): io.ktor.http.Headers = HeadersBuilder().apply {
    this@toKtorHeaders?.values?.forEach { (name, values) ->
        values.forEach { value ->
            append(name, value)
        }
    }
}.build()

private fun HttpBody?.toKtorBody(): Any = when (this) {
    null -> ByteReadChannel("")
    is JsonBody -> ByteReadChannel(body)
    else -> toString()
}
