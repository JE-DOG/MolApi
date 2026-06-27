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

`interface ApiMock<Request : ApiRequest, Matcher : ApiRequestMatcher<Request>, Response : ApiResponse, Id : Any>`
-
`data class BaseApiMock<Request : ApiRequest, Matcher : ApiRequestMatcher<Request>, Response : ApiResponse, Id : Any>`
-
`interface ApiMockIdGenerator<Matcher : ApiRequestMatcher<*>, Response : ApiResponse, Id : Any>`
-
`class HashCodeApiMockIdGenerator<Matcher : ApiRequestMatcher<*>, Response : ApiResponse>`
-
`interface ReadApiMockRegistry<Request : ApiRequest, Matcher : ApiRequestMatcher<Request>, Response : ApiResponse, Id : Any>`
-
`interface ApiMockRegistry<Request : ApiRequest, Matcher : ApiRequestMatcher<Request>, Response : ApiResponse, Id : Any>`
-
`class InMemoryApiMockRegistry<Request : ApiRequest, Matcher : ApiRequestMatcher<Request>, Response : ApiResponse, Id : Any>`

Критичные детали:

- `ApiRequestMatcher` использует метод `matches`, не `match`.
- `ApiMock.id` имеет тип `Id : Any`.
- `ApiMockIdGenerator.generateId(matcher, response)` формирует id только из `matcher` и `response`;
  текущее значение `ApiMock.id` в генерации не участвует.
- `ReadApiMockRegistry.find(request)` возвращает `ApiMock<Request, Matcher, Response, Id>?`.
- Основной `ApiMockRegistry.add(response, matcher)` сам формирует id.
- Extension `ApiMockRegistry.add(mock)` передает в основной `add` только `mock.response` и
  `mock.matcher`; id входного mock не используется.
- `remove(id: Id): Boolean` удаляет все mocks с таким id и возвращает `true`, если удалено хотя бы
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

- `interface HttpApiMock : ApiMock<HttpRequest, ApiRequestMatcher<HttpRequest>, HttpResponse, Any>`
- `open class BaseHttpRequestMatcher(...) : ApiRequestMatcher<HttpRequest>`
- `data class BaseHttpApiMock(...) : HttpApiMock`
-

`typealias HttpApiMockRegistry = ApiMockRegistry<HttpRequest, ApiRequestMatcher<HttpRequest>, HttpResponse, Any>`
-
`class HttpInMemoryApiMockRegistry : InMemoryApiMockRegistry<HttpRequest, ApiRequestMatcher<HttpRequest>, HttpResponse, Any>`

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
  `ReadApiMockRegistry<HttpRequest, ApiRequestMatcher<HttpRequest>, HttpResponse, Any>`.
- Plugin работает на Ktor `Send` hook: если registry находит mock, возвращается mock response;
  если mock не найден или метод request не поддержан текущей HTTP-моделью, запрос идет дальше
  как реальный Ktor request.

Ограничения Ktor module:

- Не добавлять server-side Ktor API.
- Не добавлять semantic JSON matching.
- Не добавлять delay/fallback-настройки сверх простого "mock найден -> mock response, иначе
  proceed".
- Пока поддержанные методы только те, что есть в `HttpMethod`: GET, POST, PUT, PATCH, DELETE.
