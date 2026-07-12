package dag.khinkal.molapi.http.dsl

import dag.khinkal.molapi.http.matcher.BaseHttpRequestMatcher
import dag.khinkal.molapi.http.model.Headers
import dag.khinkal.molapi.http.model.HttpBody
import dag.khinkal.molapi.http.model.HttpMethod
import dag.khinkal.molapi.http.model.HttpResponse
import dag.khinkal.molapi.http.model.HttpUrl
import dag.khinkal.molapi.http.model.JsonBody
import dag.khinkal.molapi.http.registry.HttpApiMockRegistry

public fun HttpApiMockRegistry.http(
    method: HttpMethod,
    path: String? = null,
    scheme: String? = null,
    host: String? = null,
    port: Int? = null,
    queryParameters: Map<String, List<String>> = emptyMap(),
    headers: Headers? = null,
    body: HttpBody? = null,
    response: () -> HttpResponse,
): Unit = http(
    method = method,
    url = HttpUrl(
        scheme = scheme,
        host = host,
        port = port,
        path = path,
        queryParameters = queryParameters,
    ),
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.http(
    method: HttpMethod,
    path: String? = null,
    scheme: String? = null,
    host: String? = null,
    port: Int? = null,
    queryParameters: Map<String, List<String>> = emptyMap(),
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpResponse,
): Unit = http(
    method = method,
    url = HttpUrl(
        scheme = scheme,
        host = host,
        port = port,
        path = path,
        queryParameters = queryParameters,
    ),
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.http(
    method: HttpMethod,
    path: String? = null,
    scheme: String? = null,
    host: String? = null,
    port: Int? = null,
    queryParameters: Map<String, List<String>> = emptyMap(),
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpBody?,
    responseHeaders: Headers? = null,
    statusCode: Int = 200,
): Unit = http(
    method = method,
    url = HttpUrl(
        scheme = scheme,
        host = host,
        port = port,
        path = path,
        queryParameters = queryParameters,
    ),
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
    path: String? = null,
    scheme: String? = null,
    host: String? = null,
    port: Int? = null,
    queryParameters: Map<String, List<String>> = emptyMap(),
    headers: Headers? = null,
    body: HttpBody? = null,
    json: String,
    responseHeaders: Headers? = Headers.jsonContent(),
    statusCode: Int = 200,
): Unit = http(
    method = method,
    url = HttpUrl(
        scheme = scheme,
        host = host,
        port = port,
        path = path,
        queryParameters = queryParameters,
    ),
    headers = headers,
    body = body,
    response = HttpResponse(
        body = JsonBody(json),
        headers = responseHeaders,
        statusCode = statusCode,
    ),
)

public fun HttpApiMockRegistry.http(
    method: HttpMethod,
    url: HttpUrl,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: () -> HttpResponse,
): Unit = registerHttpMock(
    method = method,
    url = url,
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.http(
    method: HttpMethod,
    url: HttpUrl,
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
    url: HttpUrl,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpBody?,
    responseHeaders: Headers? = null,
    statusCode: Int = 200,
): Unit = registerHttpMock(
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
    url: HttpUrl,
    headers: Headers? = null,
    body: HttpBody? = null,
    json: String,
    responseHeaders: Headers? = Headers.jsonContent(),
    statusCode: Int = 200,
): Unit = registerHttpMock(
    method = method,
    url = url,
    headers = headers,
    body = body,
    response = HttpResponse(
        body = JsonBody(json),
        headers = responseHeaders,
        statusCode = statusCode,
    ),
)

public fun HttpApiMockRegistry.get(
    path: String? = null,
    scheme: String? = null,
    host: String? = null,
    port: Int? = null,
    queryParameters: Map<String, List<String>> = emptyMap(),
    headers: Headers? = null,
    body: HttpBody? = null,
    response: () -> HttpResponse,
): Unit = get(
    url = HttpUrl(
        scheme = scheme,
        host = host,
        port = port,
        path = path,
        queryParameters = queryParameters,
    ),
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.get(
    path: String? = null,
    scheme: String? = null,
    host: String? = null,
    port: Int? = null,
    queryParameters: Map<String, List<String>> = emptyMap(),
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpResponse,
): Unit = get(
    url = HttpUrl(
        scheme = scheme,
        host = host,
        port = port,
        path = path,
        queryParameters = queryParameters,
    ),
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.get(
    path: String? = null,
    scheme: String? = null,
    host: String? = null,
    port: Int? = null,
    queryParameters: Map<String, List<String>> = emptyMap(),
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpBody?,
    responseHeaders: Headers? = null,
    statusCode: Int = 200,
): Unit = get(
    url = HttpUrl(
        scheme = scheme,
        host = host,
        port = port,
        path = path,
        queryParameters = queryParameters,
    ),
    headers = headers,
    body = body,
    response = HttpResponse(
        body = response,
        headers = responseHeaders,
        statusCode = statusCode,
    ),
)

public fun HttpApiMockRegistry.get(
    path: String? = null,
    scheme: String? = null,
    host: String? = null,
    port: Int? = null,
    queryParameters: Map<String, List<String>> = emptyMap(),
    headers: Headers? = null,
    body: HttpBody? = null,
    json: String,
    responseHeaders: Headers? = Headers.jsonContent(),
    statusCode: Int = 200,
): Unit = get(
    url = HttpUrl(
        scheme = scheme,
        host = host,
        port = port,
        path = path,
        queryParameters = queryParameters,
    ),
    headers = headers,
    body = body,
    response = HttpResponse(
        body = JsonBody(json),
        headers = responseHeaders,
        statusCode = statusCode,
    ),
)

public fun HttpApiMockRegistry.get(
    url: HttpUrl,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: () -> HttpResponse,
): Unit = registerHttpMock(
    method = HttpMethod.GET,
    url = url,
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.get(
    url: HttpUrl,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpResponse,
): Unit = registerHttpMock(
    method = HttpMethod.GET,
    url = url,
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.get(
    url: HttpUrl,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpBody?,
    responseHeaders: Headers? = null,
    statusCode: Int = 200,
): Unit = registerHttpMock(
    method = HttpMethod.GET,
    url = url,
    headers = headers,
    body = body,
    response = HttpResponse(
        body = response,
        headers = responseHeaders,
        statusCode = statusCode,
    ),
)

public fun HttpApiMockRegistry.get(
    url: HttpUrl,
    headers: Headers? = null,
    body: HttpBody? = null,
    json: String,
    responseHeaders: Headers? = Headers.jsonContent(),
    statusCode: Int = 200,
): Unit = registerHttpMock(
    method = HttpMethod.GET,
    url = url,
    headers = headers,
    body = body,
    response = HttpResponse(
        body = JsonBody(json),
        headers = responseHeaders,
        statusCode = statusCode,
    ),
)

public fun HttpApiMockRegistry.post(
    path: String? = null,
    scheme: String? = null,
    host: String? = null,
    port: Int? = null,
    queryParameters: Map<String, List<String>> = emptyMap(),
    headers: Headers? = null,
    body: HttpBody? = null,
    response: () -> HttpResponse,
): Unit = post(
    url = HttpUrl(
        scheme = scheme,
        host = host,
        port = port,
        path = path,
        queryParameters = queryParameters,
    ),
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.post(
    path: String? = null,
    scheme: String? = null,
    host: String? = null,
    port: Int? = null,
    queryParameters: Map<String, List<String>> = emptyMap(),
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpResponse,
): Unit = post(
    url = HttpUrl(
        scheme = scheme,
        host = host,
        port = port,
        path = path,
        queryParameters = queryParameters,
    ),
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.post(
    path: String? = null,
    scheme: String? = null,
    host: String? = null,
    port: Int? = null,
    queryParameters: Map<String, List<String>> = emptyMap(),
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpBody?,
    responseHeaders: Headers? = null,
    statusCode: Int = 200,
): Unit = post(
    url = HttpUrl(
        scheme = scheme,
        host = host,
        port = port,
        path = path,
        queryParameters = queryParameters,
    ),
    headers = headers,
    body = body,
    response = HttpResponse(
        body = response,
        headers = responseHeaders,
        statusCode = statusCode,
    ),
)

public fun HttpApiMockRegistry.post(
    path: String? = null,
    scheme: String? = null,
    host: String? = null,
    port: Int? = null,
    queryParameters: Map<String, List<String>> = emptyMap(),
    headers: Headers? = null,
    body: HttpBody? = null,
    json: String,
    responseHeaders: Headers? = Headers.jsonContent(),
    statusCode: Int = 200,
): Unit = post(
    url = HttpUrl(
        scheme = scheme,
        host = host,
        port = port,
        path = path,
        queryParameters = queryParameters,
    ),
    headers = headers,
    body = body,
    response = HttpResponse(
        body = JsonBody(json),
        headers = responseHeaders,
        statusCode = statusCode,
    ),
)

public fun HttpApiMockRegistry.post(
    url: HttpUrl,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: () -> HttpResponse,
): Unit = registerHttpMock(
    method = HttpMethod.POST,
    url = url,
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.post(
    url: HttpUrl,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpResponse,
): Unit = registerHttpMock(
    method = HttpMethod.POST,
    url = url,
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.post(
    url: HttpUrl,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpBody?,
    responseHeaders: Headers? = null,
    statusCode: Int = 200,
): Unit = registerHttpMock(
    method = HttpMethod.POST,
    url = url,
    headers = headers,
    body = body,
    response = HttpResponse(
        body = response,
        headers = responseHeaders,
        statusCode = statusCode,
    ),
)

public fun HttpApiMockRegistry.post(
    url: HttpUrl,
    headers: Headers? = null,
    body: HttpBody? = null,
    json: String,
    responseHeaders: Headers? = Headers.jsonContent(),
    statusCode: Int = 200,
): Unit = registerHttpMock(
    method = HttpMethod.POST,
    url = url,
    headers = headers,
    body = body,
    response = HttpResponse(
        body = JsonBody(json),
        headers = responseHeaders,
        statusCode = statusCode,
    ),
)

public fun HttpApiMockRegistry.put(
    path: String? = null,
    scheme: String? = null,
    host: String? = null,
    port: Int? = null,
    queryParameters: Map<String, List<String>> = emptyMap(),
    headers: Headers? = null,
    body: HttpBody? = null,
    response: () -> HttpResponse,
): Unit = put(
    url = HttpUrl(
        scheme = scheme,
        host = host,
        port = port,
        path = path,
        queryParameters = queryParameters,
    ),
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.put(
    path: String? = null,
    scheme: String? = null,
    host: String? = null,
    port: Int? = null,
    queryParameters: Map<String, List<String>> = emptyMap(),
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpResponse,
): Unit = put(
    url = HttpUrl(
        scheme = scheme,
        host = host,
        port = port,
        path = path,
        queryParameters = queryParameters,
    ),
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.put(
    path: String? = null,
    scheme: String? = null,
    host: String? = null,
    port: Int? = null,
    queryParameters: Map<String, List<String>> = emptyMap(),
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpBody?,
    responseHeaders: Headers? = null,
    statusCode: Int = 200,
): Unit = put(
    url = HttpUrl(
        scheme = scheme,
        host = host,
        port = port,
        path = path,
        queryParameters = queryParameters,
    ),
    headers = headers,
    body = body,
    response = HttpResponse(
        body = response,
        headers = responseHeaders,
        statusCode = statusCode,
    ),
)

public fun HttpApiMockRegistry.put(
    path: String? = null,
    scheme: String? = null,
    host: String? = null,
    port: Int? = null,
    queryParameters: Map<String, List<String>> = emptyMap(),
    headers: Headers? = null,
    body: HttpBody? = null,
    json: String,
    responseHeaders: Headers? = Headers.jsonContent(),
    statusCode: Int = 200,
): Unit = put(
    url = HttpUrl(
        scheme = scheme,
        host = host,
        port = port,
        path = path,
        queryParameters = queryParameters,
    ),
    headers = headers,
    body = body,
    response = HttpResponse(
        body = JsonBody(json),
        headers = responseHeaders,
        statusCode = statusCode,
    ),
)

public fun HttpApiMockRegistry.put(
    url: HttpUrl,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: () -> HttpResponse,
): Unit = registerHttpMock(
    method = HttpMethod.PUT,
    url = url,
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.put(
    url: HttpUrl,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpResponse,
): Unit = registerHttpMock(
    method = HttpMethod.PUT,
    url = url,
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.put(
    url: HttpUrl,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpBody?,
    responseHeaders: Headers? = null,
    statusCode: Int = 200,
): Unit = registerHttpMock(
    method = HttpMethod.PUT,
    url = url,
    headers = headers,
    body = body,
    response = HttpResponse(
        body = response,
        headers = responseHeaders,
        statusCode = statusCode,
    ),
)

public fun HttpApiMockRegistry.put(
    url: HttpUrl,
    headers: Headers? = null,
    body: HttpBody? = null,
    json: String,
    responseHeaders: Headers? = Headers.jsonContent(),
    statusCode: Int = 200,
): Unit = registerHttpMock(
    method = HttpMethod.PUT,
    url = url,
    headers = headers,
    body = body,
    response = HttpResponse(
        body = JsonBody(json),
        headers = responseHeaders,
        statusCode = statusCode,
    ),
)

public fun HttpApiMockRegistry.patch(
    path: String? = null,
    scheme: String? = null,
    host: String? = null,
    port: Int? = null,
    queryParameters: Map<String, List<String>> = emptyMap(),
    headers: Headers? = null,
    body: HttpBody? = null,
    response: () -> HttpResponse,
): Unit = patch(
    url = HttpUrl(
        scheme = scheme,
        host = host,
        port = port,
        path = path,
        queryParameters = queryParameters,
    ),
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.patch(
    path: String? = null,
    scheme: String? = null,
    host: String? = null,
    port: Int? = null,
    queryParameters: Map<String, List<String>> = emptyMap(),
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpResponse,
): Unit = patch(
    url = HttpUrl(
        scheme = scheme,
        host = host,
        port = port,
        path = path,
        queryParameters = queryParameters,
    ),
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.patch(
    path: String? = null,
    scheme: String? = null,
    host: String? = null,
    port: Int? = null,
    queryParameters: Map<String, List<String>> = emptyMap(),
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpBody?,
    responseHeaders: Headers? = null,
    statusCode: Int = 200,
): Unit = patch(
    url = HttpUrl(
        scheme = scheme,
        host = host,
        port = port,
        path = path,
        queryParameters = queryParameters,
    ),
    headers = headers,
    body = body,
    response = HttpResponse(
        body = response,
        headers = responseHeaders,
        statusCode = statusCode,
    ),
)

public fun HttpApiMockRegistry.patch(
    path: String? = null,
    scheme: String? = null,
    host: String? = null,
    port: Int? = null,
    queryParameters: Map<String, List<String>> = emptyMap(),
    headers: Headers? = null,
    body: HttpBody? = null,
    json: String,
    responseHeaders: Headers? = Headers.jsonContent(),
    statusCode: Int = 200,
): Unit = patch(
    url = HttpUrl(
        scheme = scheme,
        host = host,
        port = port,
        path = path,
        queryParameters = queryParameters,
    ),
    headers = headers,
    body = body,
    response = HttpResponse(
        body = JsonBody(json),
        headers = responseHeaders,
        statusCode = statusCode,
    ),
)

public fun HttpApiMockRegistry.patch(
    url: HttpUrl,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: () -> HttpResponse,
): Unit = registerHttpMock(
    method = HttpMethod.PATCH,
    url = url,
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.patch(
    url: HttpUrl,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpResponse,
): Unit = registerHttpMock(
    method = HttpMethod.PATCH,
    url = url,
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.patch(
    url: HttpUrl,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpBody?,
    responseHeaders: Headers? = null,
    statusCode: Int = 200,
): Unit = registerHttpMock(
    method = HttpMethod.PATCH,
    url = url,
    headers = headers,
    body = body,
    response = HttpResponse(
        body = response,
        headers = responseHeaders,
        statusCode = statusCode,
    ),
)

public fun HttpApiMockRegistry.patch(
    url: HttpUrl,
    headers: Headers? = null,
    body: HttpBody? = null,
    json: String,
    responseHeaders: Headers? = Headers.jsonContent(),
    statusCode: Int = 200,
): Unit = registerHttpMock(
    method = HttpMethod.PATCH,
    url = url,
    headers = headers,
    body = body,
    response = HttpResponse(
        body = JsonBody(json),
        headers = responseHeaders,
        statusCode = statusCode,
    ),
)

public fun HttpApiMockRegistry.head(
    path: String? = null,
    scheme: String? = null,
    host: String? = null,
    port: Int? = null,
    queryParameters: Map<String, List<String>> = emptyMap(),
    headers: Headers? = null,
    body: HttpBody? = null,
    response: () -> HttpResponse,
): Unit = head(
    url = HttpUrl(
        scheme = scheme,
        host = host,
        port = port,
        path = path,
        queryParameters = queryParameters,
    ),
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.head(
    path: String? = null,
    scheme: String? = null,
    host: String? = null,
    port: Int? = null,
    queryParameters: Map<String, List<String>> = emptyMap(),
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpResponse,
): Unit = head(
    url = HttpUrl(
        scheme = scheme,
        host = host,
        port = port,
        path = path,
        queryParameters = queryParameters,
    ),
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.head(
    path: String? = null,
    scheme: String? = null,
    host: String? = null,
    port: Int? = null,
    queryParameters: Map<String, List<String>> = emptyMap(),
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpBody?,
    responseHeaders: Headers? = null,
    statusCode: Int = 200,
): Unit = head(
    url = HttpUrl(
        scheme = scheme,
        host = host,
        port = port,
        path = path,
        queryParameters = queryParameters,
    ),
    headers = headers,
    body = body,
    response = HttpResponse(
        body = response,
        headers = responseHeaders,
        statusCode = statusCode,
    ),
)

public fun HttpApiMockRegistry.head(
    path: String? = null,
    scheme: String? = null,
    host: String? = null,
    port: Int? = null,
    queryParameters: Map<String, List<String>> = emptyMap(),
    headers: Headers? = null,
    body: HttpBody? = null,
    json: String,
    responseHeaders: Headers? = Headers.jsonContent(),
    statusCode: Int = 200,
): Unit = head(
    url = HttpUrl(
        scheme = scheme,
        host = host,
        port = port,
        path = path,
        queryParameters = queryParameters,
    ),
    headers = headers,
    body = body,
    response = HttpResponse(
        body = JsonBody(json),
        headers = responseHeaders,
        statusCode = statusCode,
    ),
)

public fun HttpApiMockRegistry.head(
    url: HttpUrl,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: () -> HttpResponse,
): Unit = registerHttpMock(
    method = HttpMethod.HEAD,
    url = url,
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.head(
    url: HttpUrl,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpResponse,
): Unit = registerHttpMock(
    method = HttpMethod.HEAD,
    url = url,
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.head(
    url: HttpUrl,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpBody?,
    responseHeaders: Headers? = null,
    statusCode: Int = 200,
): Unit = registerHttpMock(
    method = HttpMethod.HEAD,
    url = url,
    headers = headers,
    body = body,
    response = HttpResponse(
        body = response,
        headers = responseHeaders,
        statusCode = statusCode,
    ),
)

public fun HttpApiMockRegistry.head(
    url: HttpUrl,
    headers: Headers? = null,
    body: HttpBody? = null,
    json: String,
    responseHeaders: Headers? = Headers.jsonContent(),
    statusCode: Int = 200,
): Unit = registerHttpMock(
    method = HttpMethod.HEAD,
    url = url,
    headers = headers,
    body = body,
    response = HttpResponse(
        body = JsonBody(json),
        headers = responseHeaders,
        statusCode = statusCode,
    ),
)

public fun HttpApiMockRegistry.delete(
    path: String? = null,
    scheme: String? = null,
    host: String? = null,
    port: Int? = null,
    queryParameters: Map<String, List<String>> = emptyMap(),
    headers: Headers? = null,
    body: HttpBody? = null,
    response: () -> HttpResponse,
): Unit = delete(
    url = HttpUrl(
        scheme = scheme,
        host = host,
        port = port,
        path = path,
        queryParameters = queryParameters,
    ),
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.delete(
    path: String? = null,
    scheme: String? = null,
    host: String? = null,
    port: Int? = null,
    queryParameters: Map<String, List<String>> = emptyMap(),
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpResponse,
): Unit = delete(
    url = HttpUrl(
        scheme = scheme,
        host = host,
        port = port,
        path = path,
        queryParameters = queryParameters,
    ),
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.delete(
    path: String? = null,
    scheme: String? = null,
    host: String? = null,
    port: Int? = null,
    queryParameters: Map<String, List<String>> = emptyMap(),
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpBody?,
    responseHeaders: Headers? = null,
    statusCode: Int = 200,
): Unit = delete(
    url = HttpUrl(
        scheme = scheme,
        host = host,
        port = port,
        path = path,
        queryParameters = queryParameters,
    ),
    headers = headers,
    body = body,
    response = HttpResponse(
        body = response,
        headers = responseHeaders,
        statusCode = statusCode,
    ),
)

public fun HttpApiMockRegistry.delete(
    path: String? = null,
    scheme: String? = null,
    host: String? = null,
    port: Int? = null,
    queryParameters: Map<String, List<String>> = emptyMap(),
    headers: Headers? = null,
    body: HttpBody? = null,
    json: String,
    responseHeaders: Headers? = Headers.jsonContent(),
    statusCode: Int = 200,
): Unit = delete(
    url = HttpUrl(
        scheme = scheme,
        host = host,
        port = port,
        path = path,
        queryParameters = queryParameters,
    ),
    headers = headers,
    body = body,
    response = HttpResponse(
        body = JsonBody(json),
        headers = responseHeaders,
        statusCode = statusCode,
    ),
)

public fun HttpApiMockRegistry.delete(
    url: HttpUrl,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: () -> HttpResponse,
): Unit = registerHttpMock(
    method = HttpMethod.DELETE,
    url = url,
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.delete(
    url: HttpUrl,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpResponse,
): Unit = registerHttpMock(
    method = HttpMethod.DELETE,
    url = url,
    headers = headers,
    body = body,
    response = response,
)

public fun HttpApiMockRegistry.delete(
    url: HttpUrl,
    headers: Headers? = null,
    body: HttpBody? = null,
    response: HttpBody?,
    responseHeaders: Headers? = null,
    statusCode: Int = 200,
): Unit = registerHttpMock(
    method = HttpMethod.DELETE,
    url = url,
    headers = headers,
    body = body,
    response = HttpResponse(
        body = response,
        headers = responseHeaders,
        statusCode = statusCode,
    ),
)

public fun HttpApiMockRegistry.delete(
    url: HttpUrl,
    headers: Headers? = null,
    body: HttpBody? = null,
    json: String,
    responseHeaders: Headers? = Headers.jsonContent(),
    statusCode: Int = 200,
): Unit = registerHttpMock(
    method = HttpMethod.DELETE,
    url = url,
    headers = headers,
    body = body,
    response = HttpResponse(
        body = JsonBody(json),
        headers = responseHeaders,
        statusCode = statusCode,
    ),
)

private fun HttpApiMockRegistry.registerHttpMock(
    method: HttpMethod,
    url: HttpUrl,
    headers: Headers?,
    body: HttpBody?,
    response: () -> HttpResponse,
) {
    registerHttpMock(
        method = method,
        url = url,
        headers = headers,
        body = body,
        response = response(),
    )
}

private fun HttpApiMockRegistry.registerHttpMock(
    method: HttpMethod,
    url: HttpUrl,
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
