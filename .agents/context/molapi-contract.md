# MolApi Contract

## Scope

Библиотека `dag.khinkal.molapi` хранит список mock-правил. Каждое правило содержит:

- `id`
- `matcher`
- `response`

Базовый поток:

```text
request -> registry.find(request) -> mock.matcher.matches(request) -> mock.response
```

## Core module

Path: `molapi-core`

Package: `dag.khinkal.molapi.core`

Сущности core:

- `interface ApiRequest`
- `interface ApiResponse`
- `interface ApiRequestMatcher<AR : ApiRequest> { fun matches(request: AR): Boolean }`
-

`interface ApiMock<Request : ApiRequest, Matcher : ApiRequestMatcher<Request>, Response : ApiResponse>`
-
`data class BaseApiMock<Request : ApiRequest, Matcher : ApiRequestMatcher<Request>, Response : ApiResponse>`
-
`interface ReadApiMockRegistry<Request : ApiRequest, Matcher : ApiRequestMatcher<Request>, Response : ApiResponse>`
-
`interface ApiMockRegistry<Request : ApiRequest, Matcher : ApiRequestMatcher<Request>, Response : ApiResponse>`
-
`class InMemoryApiMockRegistry<Request : ApiRequest, Matcher : ApiRequestMatcher<Request>, Response : ApiResponse>`

Критичные детали:

- `ApiRequestMatcher` использует метод `matches`, не `match`.
- `ApiMock.id` имеет тип `Any`.
- `ReadApiMockRegistry.find(request)` возвращает `ApiMock<Request, Matcher, Response>?`.
- `ApiMockRegistry.add` принимает `ApiMock<Request, Matcher, Response>`.
- `remove(id: Any): Boolean` удаляет все mocks с таким id и возвращает `true`, если удалено хотя бы
  одно.
- `clear()` ничего не возвращает.
- `find` возвращает первый mock, для которого `mock.matcher.matches(request) == true`.

## HTTP module

Path: `molapi-http`

Package: `dag.khinkal.molapi.http`

`molapi-http` зависит от `molapi-core`.

Сущности HTTP:

- `enum class HttpMethod { GET, POST, PUT, PATCH, DELETE }`
- `data class Headers(val values: Map<String, Set<String>>)`, с `Headers.empty()`
- `interface HttpBody`
- `data class JsonBody(val body: String) : HttpBody`
-

`data class HttpRequest(val url: String, val headers: Headers?, val body: HttpBody, val method: HttpMethod) : ApiRequest`
-
`data class HttpResponse(val headers: Headers?, val body: HttpBody, val statusCode: Int = 200) : ApiResponse`

- `interface HttpApiMock : ApiMock<HttpRequest, ApiRequestMatcher<HttpRequest>, HttpResponse>`
- `open class BaseHttpRequestMatcher(...) : ApiRequestMatcher<HttpRequest>`
- `data class BaseHttpApiMock(...) : HttpApiMock`
-

`typealias HttpMockRegistry = InMemoryApiMockRegistry<HttpRequest, ApiRequestMatcher<HttpRequest>, HttpResponse>`

`BaseHttpRequestMatcher`:

- если `url != null`, `request.url` должен быть равен `url`;
- если `method != null`, `request.method` должен быть равен `method`;
- если `body != null`, `request.body` должен быть равен `body`;
- если все заданные условия совпали, вернуть `true`;
- если хотя бы одно заданное условие не совпало, вернуть `false`;
- если все параметры matcher-а `null`, matcher возвращает `true` для любого `HttpRequest`.

## Explicitly Out Of Scope

Не реализовывать без отдельного запроса:

- Ktor plugin
- OkHttp adapter
- полноценную `Url` модель
- query params parsing
- `ApiKey`
- header matching
- JSON semantic matching
- delay у response
- fallback to real request

## Ktor module

Path: `molapi-http-ktor`

Package: `dag.khinkal.molapi.http.ktor`

`molapi-http-ktor` адаптирует `molapi-http` под Ktor Client:

- `HttpRequestBuilder.toMolApiHttpRequestOrNull()` преобразует поддержанные Ktor requests
  в `dag.khinkal.molapi.http.HttpRequest`.
- `HttpResponse.toKtorHttpClientCall(...)` преобразует `dag.khinkal.molapi.http.HttpResponse`
  в Ktor `HttpClientCall`.
- `MolApiKtorPlugin` принимает
  `ReadApiMockRegistry<HttpRequest, ApiRequestMatcher<HttpRequest>, HttpResponse>`.
- Plugin работает на Ktor `Send` hook: если registry находит mock, возвращается mock response;
  если mock не найден или метод request не поддержан текущей HTTP-моделью, запрос идет дальше
  как реальный Ktor request.

Ограничения Ktor module:

- Не добавлять server-side Ktor API.
- Не добавлять semantic JSON matching.
- Не добавлять delay/fallback-настройки сверх простого "mock найден -> mock response, иначе
  proceed".
- Пока поддержанные методы только те, что есть в `HttpMethod`: GET, POST, PUT, PATCH, DELETE.
