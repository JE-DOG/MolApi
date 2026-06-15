package dag.khinkal.molapi.http.serializable.util

import dag.khinkal.molapi.http.model.Headers
import dag.khinkal.molapi.http.model.HttpResponse
import dag.khinkal.molapi.http.model.JsonBody
import kotlinx.serialization.json.Json

public inline fun <reified T : Any> SerializableJsonBody(
    body: T,
    json: Json = Json,
): JsonBody = JsonBody(
    body = json.encodeToString(body),
)

public inline fun <reified T : Any> JsonHttpResponse(
    serializableBody: T,
    headers: Headers? = Headers.jsonContent(),
    statusCode: Int = 200,
    json: Json = Json,
): HttpResponse = HttpResponse(
    headers = headers,
    body = SerializableJsonBody(
        body = serializableBody,
        json = json,
    ),
    statusCode = statusCode,
)
