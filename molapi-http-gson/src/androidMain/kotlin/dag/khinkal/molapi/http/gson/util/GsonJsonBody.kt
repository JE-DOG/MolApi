package dag.khinkal.molapi.http.gson.util

import com.google.gson.Gson
import dag.khinkal.molapi.http.model.Headers
import dag.khinkal.molapi.http.model.HttpResponse
import dag.khinkal.molapi.http.model.JsonBody

public inline fun <reified T : Any> GsonHttpResponse(
    headers: Headers?,
    body: T,
    statusCode: Int = 200,
    gson: Gson = Gson(),
): HttpResponse = HttpResponse(
    headers = headers,
    body = GsonJsonBody(
        data = body,
        gson = gson,
    ),
    statusCode = statusCode,
)

public inline fun <reified T : Any> GsonJsonBody(
    data: T,
    gson: Gson = Gson(),
): JsonBody = JsonBody(
    body = gson.toJson(data),
)
