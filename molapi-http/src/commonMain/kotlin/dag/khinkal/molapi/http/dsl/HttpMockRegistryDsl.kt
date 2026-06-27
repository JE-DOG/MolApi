package dag.khinkal.molapi.http.dsl

import dag.khinkal.molapi.http.matcher.BaseHttpRequestMatcher
import dag.khinkal.molapi.http.model.Headers
import dag.khinkal.molapi.http.model.HttpBody
import dag.khinkal.molapi.http.model.HttpMethod
import dag.khinkal.molapi.http.model.HttpResponse
import dag.khinkal.molapi.http.model.JsonBody
import dag.khinkal.molapi.http.registry.HttpApiMockRegistry

public fun HttpApiMockRegistry.http(
    method: HttpMethod,
    url: String,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: () -> HttpResponse,
): Unit = http(
    method = method,
    url = url,
    headers = headers,
    body = body,
    response = response(),
)

public fun HttpApiMockRegistry.http(
    method: HttpMethod,
    url: String,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpResponse,
): Unit = registerHttpMock(
    method = method,
    url = url,
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.http(
    method: HttpMethod,
    url: String,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpBody?,
    responseHeaders: Headers? = null,
    statusCode: Int = 200,
): Unit = http(
    method = method,
    url = url,
    headers = headers,
    body = body,
    response = HttpResponse(
        body = response,
        headers = responseHeaders,
        statusCode = statusCode,
    ),
)

public fun HttpApiMockRegistry.http(
    method: HttpMethod,
    url: String,
    headers: Headers? = null,
    body: HttpBody? = null,
    json: String,
    responseHeaders: Headers? = Headers.jsonContent(),
    statusCode: Int = 200,
): Unit = http(
    method = method,
    url = url,
    headers = headers,
    body = body,
    response = JsonBody(json),
    responseHeaders = responseHeaders,
    statusCode = statusCode,
)

public fun HttpApiMockRegistry.get(
    url: String,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: () -> HttpResponse,
): Unit = http(
    method = HttpMethod.GET,
    url = url,
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.get(
    url: String,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpResponse,
): Unit = http(
    method = HttpMethod.GET,
    url = url,
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.get(
    url: String,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpBody?,
    responseHeaders: Headers? = null,
    statusCode: Int = 200,
): Unit = http(
    method = HttpMethod.GET,
    url = url,
    headers = headers,
    body = body,
    response = response,
    responseHeaders = responseHeaders,
    statusCode = statusCode,
)

public fun HttpApiMockRegistry.get(
    url: String,
    headers: Headers? = null,
    body: HttpBody? = null,
    json: String,
    responseHeaders: Headers? = Headers.jsonContent(),
    statusCode: Int = 200,
): Unit = http(
    method = HttpMethod.GET,
    url = url,
    headers = headers,
    body = body,
    json = json,
    responseHeaders = responseHeaders,
    statusCode = statusCode,
)

public fun HttpApiMockRegistry.post(
    url: String,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: () -> HttpResponse,
): Unit = http(
    method = HttpMethod.POST,
    url = url,
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.post(
    url: String,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpResponse,
): Unit = http(
    method = HttpMethod.POST,
    url = url,
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.post(
    url: String,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpBody?,
    responseHeaders: Headers? = null,
    statusCode: Int = 200,
): Unit = http(
    method = HttpMethod.POST,
    url = url,
    headers = headers,
    body = body,
    response = response,
    responseHeaders = responseHeaders,
    statusCode = statusCode,
)

public fun HttpApiMockRegistry.post(
    url: String,
    headers: Headers? = null,
    body: HttpBody? = null,
    json: String,
    responseHeaders: Headers? = Headers.jsonContent(),
    statusCode: Int = 200,
): Unit = http(
    method = HttpMethod.POST,
    url = url,
    headers = headers,
    body = body,
    json = json,
    responseHeaders = responseHeaders,
    statusCode = statusCode,
)

public fun HttpApiMockRegistry.put(
    url: String,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: () -> HttpResponse,
): Unit = http(
    method = HttpMethod.PUT,
    url = url,
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.put(
    url: String,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpResponse,
): Unit = http(
    method = HttpMethod.PUT,
    url = url,
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.put(
    url: String,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpBody?,
    responseHeaders: Headers? = null,
    statusCode: Int = 200,
): Unit = http(
    method = HttpMethod.PUT,
    url = url,
    headers = headers,
    body = body,
    response = response,
    responseHeaders = responseHeaders,
    statusCode = statusCode,
)

public fun HttpApiMockRegistry.put(
    url: String,
    headers: Headers? = null,
    body: HttpBody? = null,
    json: String,
    responseHeaders: Headers? = Headers.jsonContent(),
    statusCode: Int = 200,
): Unit = http(
    method = HttpMethod.PUT,
    url = url,
    headers = headers,
    body = body,
    json = json,
    responseHeaders = responseHeaders,
    statusCode = statusCode,
)

public fun HttpApiMockRegistry.patch(
    url: String,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: () -> HttpResponse,
): Unit = http(
    method = HttpMethod.PATCH,
    url = url,
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.patch(
    url: String,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpResponse,
): Unit = http(
    method = HttpMethod.PATCH,
    url = url,
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.patch(
    url: String,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpBody?,
    responseHeaders: Headers? = null,
    statusCode: Int = 200,
): Unit = http(
    method = HttpMethod.PATCH,
    url = url,
    headers = headers,
    body = body,
    response = response,
    responseHeaders = responseHeaders,
    statusCode = statusCode,
)

public fun HttpApiMockRegistry.patch(
    url: String,
    headers: Headers? = null,
    body: HttpBody? = null,
    json: String,
    responseHeaders: Headers? = Headers.jsonContent(),
    statusCode: Int = 200,
): Unit = http(
    method = HttpMethod.PATCH,
    url = url,
    headers = headers,
    body = body,
    json = json,
    responseHeaders = responseHeaders,
    statusCode = statusCode,
)

public fun HttpApiMockRegistry.head(
    url: String,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: () -> HttpResponse,
): Unit = http(
    method = HttpMethod.HEAD,
    url = url,
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.head(
    url: String,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpResponse,
): Unit = http(
    method = HttpMethod.HEAD,
    url = url,
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.head(
    url: String,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpBody?,
    responseHeaders: Headers? = null,
    statusCode: Int = 200,
): Unit = http(
    method = HttpMethod.HEAD,
    url = url,
    headers = headers,
    body = body,
    response = response,
    responseHeaders = responseHeaders,
    statusCode = statusCode,
)

public fun HttpApiMockRegistry.head(
    url: String,
    headers: Headers? = null,
    body: HttpBody? = null,
    json: String,
    responseHeaders: Headers? = Headers.jsonContent(),
    statusCode: Int = 200,
): Unit = http(
    method = HttpMethod.HEAD,
    url = url,
    headers = headers,
    body = body,
    json = json,
    responseHeaders = responseHeaders,
    statusCode = statusCode,
)

public fun HttpApiMockRegistry.delete(
    url: String,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: () -> HttpResponse,
): Unit = http(
    method = HttpMethod.DELETE,
    url = url,
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.delete(
    url: String,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpResponse,
): Unit = http(
    method = HttpMethod.DELETE,
    url = url,
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.delete(
    url: String,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpBody?,
    responseHeaders: Headers? = null,
    statusCode: Int = 200,
): Unit = http(
    method = HttpMethod.DELETE,
    url = url,
    headers = headers,
    body = body,
    response = response,
    responseHeaders = responseHeaders,
    statusCode = statusCode,
)

public fun HttpApiMockRegistry.delete(
    url: String,
    headers: Headers? = null,
    body: HttpBody? = null,
    json: String,
    responseHeaders: Headers? = Headers.jsonContent(),
    statusCode: Int = 200,
): Unit = http(
    method = HttpMethod.DELETE,
    url = url,
    headers = headers,
    body = body,
    json = json,
    responseHeaders = responseHeaders,
    statusCode = statusCode,
)

private fun HttpApiMockRegistry.registerHttpMock(
    method: HttpMethod,
    url: String,
    headers: Headers?,
    body: HttpBody?,
    response: HttpResponse,
) {
    add(
        response = response,
        matcher = BaseHttpRequestMatcher(
            url = url,
            method = method,
            headers = headers,
            body = body,
        ),
    )
}
