package dag.khinkal.molapi.http.retrofit.util

import dag.khinkal.molapi.http.model.Headers
import dag.khinkal.molapi.http.model.HttpBody
import dag.khinkal.molapi.http.model.HttpResponse
import dag.khinkal.molapi.http.model.JsonHttpBody
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody

public fun HttpResponse.toOkHttpResponse(request: Request): Response {
    val createdAtMillis = System.currentTimeMillis()
    val mediaType = headers.contentTypeOrNull()
    return Response.Builder()
        .request(request)
        .protocol(Protocol.HTTP_1_1)
        .code(statusCode)
        .message("")
        .headers(headers.toOkHttpHeaders())
        .apply {
            val body = body ?: return@apply

            body(body.toOkHttpBody(mediaType))
        }
        .sentRequestAtMillis(createdAtMillis)
        .receivedResponseAtMillis(createdAtMillis)
        .build()
}

private fun Headers?.toOkHttpHeaders(): okhttp3.Headers = okhttp3.Headers.Builder().apply {
    this@toOkHttpHeaders?.values?.forEach { (name, values) ->
        values.forEach { value ->
            add(name, value)
        }
    }
}.build()

private fun Headers?.contentTypeOrNull(): MediaType? =
    this
        ?.values
        ?.entries
        ?.firstOrNull { (name, _) -> name.equals("Content-Type", ignoreCase = true) }
        ?.value
        ?.firstOrNull()
        ?.toMediaTypeOrNull()

private fun HttpBody.toOkHttpBody(mediaType: MediaType?): ResponseBody = when (this) {
    is JsonHttpBody -> body.toResponseBody(mediaType)
    else -> toString().toResponseBody(mediaType)
}
