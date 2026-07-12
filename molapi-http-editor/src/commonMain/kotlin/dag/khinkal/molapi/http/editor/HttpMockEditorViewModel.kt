package dag.khinkal.molapi.http.editor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dag.khinkal.molapi.core.matcher.ApiRequestMatcher
import dag.khinkal.molapi.core.model.ApiMock
import dag.khinkal.molapi.http.matcher.BaseHttpRequestMatcher
import dag.khinkal.molapi.http.matcher.RawHttpUrlRequestMatcher
import dag.khinkal.molapi.http.model.Headers
import dag.khinkal.molapi.http.model.HttpBody
import dag.khinkal.molapi.http.model.HttpMethod
import dag.khinkal.molapi.http.model.HttpRequest
import dag.khinkal.molapi.http.model.HttpResponse
import dag.khinkal.molapi.http.model.HttpUrl
import dag.khinkal.molapi.http.model.JsonBody
import dag.khinkal.molapi.http.registry.HttpApiMockRegistry

internal class HttpMockEditorViewModel(
    private val registry: HttpApiMockRegistry,
) : ViewModel() {

    val mocks = registry.mocks

    var destination: HttpMockEditorDestination by mutableStateOf(HttpMockEditorDestination.List)
        private set

    var searchQuery: String by mutableStateOf("")
        private set

    var draft: HttpMockEditorDraft by mutableStateOf(HttpMockEditorDraft.initial())
        private set

    fun showCreateMock() {
        destination = HttpMockEditorDestination.Create
    }

    fun showMockList() {
        destination = HttpMockEditorDestination.List
    }

    fun onSearchQueryChanged(value: String) {
        searchQuery = value
    }

    fun onDraftMatcherUrlChanged(value: String) = updateDraft {
        copy(matcherUrl = value, error = null)
    }

    fun onDraftMatcherMethodChanged(value: HttpMethod?) =
        updateDraft { copy(matcherMethod = value, error = null) }

    fun onDraftMatcherHeadersChanged(value: String) = updateDraft {
        copy(matcherHeaders = value, error = null)
    }

    fun onDraftMatcherBodyChanged(value: String) = updateDraft {
        copy(matcherBody = value, error = null)
    }

    fun onDraftResponseStatusCodeChanged(value: String) =
        updateDraft { copy(responseStatusCode = value, error = null) }

    fun onDraftResponseHeadersChanged(value: String) = updateDraft {
        copy(responseHeaders = value, error = null)
    }

    fun onDraftResponseBodyChanged(value: String) = updateDraft {
        copy(responseBody = value, error = null)
    }

    fun onDraftMatcherBodyDocumentSelected(body: String) = updateDraft {
        copy(matcherBody = body, error = null)
    }

    fun onDraftResponseBodyDocumentSelected(body: String) = updateDraft {
        copy(responseBody = body, error = null)
    }

    fun onJsonDocumentReadFailed() = updateDraft {
        copy(error = HttpMockEditorDraftError.JsonDocumentReadFailed)
    }

    fun visibleMocks(
        mocks: List<ApiMock<HttpRequest, ApiRequestMatcher<HttpRequest>, HttpResponse, Any>>,
    ): List<HttpMockEditorMock> {
        val queryTokens = searchQuery
            .trim()
            .lowercase()
            .split(Regex("\\s+"))
            .filter(String::isNotEmpty)

        return mocks
            .map(ApiMock<HttpRequest, *, HttpResponse, *>::toEditorMock)
            .filter { mock ->
                queryTokens.isEmpty() || queryTokens.all(mock.searchText()::contains)
            }
    }

    fun removeMock(mock: HttpMockEditorMock): Boolean = registry.remove(mock.id)

    fun clearMocks() {
        registry.clear()
    }

    fun addDraftMock(): Boolean {
        val currentDraft = draft
        val statusCode = currentDraft.responseStatusCode.trim().toIntOrNull()
        if (statusCode == null) {
            updateDraft { copy(error = HttpMockEditorDraftError.StatusCodeMustBeNumber) }
            return false
        }
        if (statusCode !in HTTP_STATUS_CODE_RANGE) {
            updateDraft { copy(error = HttpMockEditorDraftError.StatusCodeOutOfRange) }
            return false
        }
        val matcherHeaders = currentDraft.matcherHeaders
            .takeIf(String::isNotBlank)
            ?.let { value ->
                parseHeaders(value) { lineNumber ->
                    HttpMockEditorDraftError.InvalidMatcherHeaderLine(lineNumber)
                }
            }
            ?: if (currentDraft.matcherHeaders.isBlank()) null else return false
        val responseHeaders = currentDraft.responseHeaders
            .takeIf(String::isNotBlank)
            ?.let { value ->
                parseHeaders(value) { lineNumber ->
                    HttpMockEditorDraftError.InvalidResponseHeaderLine(lineNumber)
                }
            }
            ?: if (currentDraft.responseHeaders.isBlank()) null else return false
        registry.add(
            matcher = RawHttpUrlRequestMatcher(
                rawUrl = currentDraft.matcherUrl.trim(),
                method = currentDraft.matcherMethod,
                body = currentDraft.matcherBody.trim().takeIf(String::isNotEmpty)?.let(::JsonBody),
                headers = matcherHeaders,
            ),
            response = HttpResponse(
                body = currentDraft.responseBody.takeIf(String::isNotBlank)?.let(::JsonBody),
                headers = responseHeaders,
                statusCode = statusCode,
            ),
        )
        draft = HttpMockEditorDraft.initial()
        return true
    }

    private fun updateDraft(update: HttpMockEditorDraft.() -> HttpMockEditorDraft) {
        draft = draft.update()
    }

    private fun parseHeaders(
        value: String,
        invalidLineError: (Int) -> HttpMockEditorDraftError,
    ): Headers? {
        val headerValues = linkedMapOf<String, MutableList<String>>()
        value.lineSequence()
            .map(String::trim)
            .filter(String::isNotEmpty)
            .forEachIndexed { index, line ->
                val separatorIndex = line.indexOf(':')
                val name = line.substringBefore(':').trim()
                val headerValue = line.substringAfter(':', missingDelimiterValue = "").trim()
                if (separatorIndex <= 0 || name.isEmpty() || headerValue.isEmpty()) {
                    updateDraft { copy(error = invalidLineError(index + 1)) }
                    return null
                }
                headerValues.getOrPut(name, ::mutableListOf).add(headerValue)
            }
        updateDraft { copy(error = null) }
        return headerValues.takeIf(Map<*, *>::isNotEmpty)?.let(::Headers)
    }
}

internal enum class HttpMockEditorDestination {
    List,
    Create,
}

internal data class HttpMockEditorDraft(
    val matcherUrl: String,
    val matcherMethod: HttpMethod?,
    val matcherHeaders: String,
    val matcherBody: String,
    val responseStatusCode: String,
    val responseHeaders: String,
    val responseBody: String,
    val error: HttpMockEditorDraftError?,
) {

    companion object {

        fun initial(): HttpMockEditorDraft = HttpMockEditorDraft(
            matcherUrl = "",
            matcherMethod = HttpMethod.GET,
            matcherHeaders = "",
            matcherBody = "",
            responseStatusCode = "200",
            responseHeaders = "Content-Type: application/json\n",
            responseBody = "",
            error = null,
        )
    }
}

internal data class HttpMockEditorMock(
    val id: Any,
    val urlText: String?,
    val method: HttpMethod?,
    val requestHeaders: String?,
    val requestBody: String?,
    val responseHeaders: String?,
    val statusCode: Int,
    val responseBody: String?,
    val matcherDescription: String,
    val isCustomMatcher: Boolean,
)

internal sealed interface HttpMockEditorDraftError {
    data object StatusCodeMustBeNumber : HttpMockEditorDraftError
    data object StatusCodeOutOfRange : HttpMockEditorDraftError
    data class InvalidMatcherHeaderLine(val lineNumber: Int) : HttpMockEditorDraftError
    data class InvalidResponseHeaderLine(val lineNumber: Int) : HttpMockEditorDraftError
    data object JsonDocumentReadFailed : HttpMockEditorDraftError
}

private fun ApiMock<HttpRequest, *, HttpResponse, *>.toEditorMock(): HttpMockEditorMock {
    val baseMatcher = matcher as? BaseHttpRequestMatcher
    val rawUrlMatcher = matcher as? RawHttpUrlRequestMatcher
    return HttpMockEditorMock(
        id = id,
        urlText = (rawUrlMatcher?.rawUrl ?: baseMatcher?.url?.asDescription())
            ?.takeIf(String::isNotBlank),
        method = rawUrlMatcher?.method ?: baseMatcher?.method,
        requestHeaders = (rawUrlMatcher?.headers ?: baseMatcher?.headers).asText(),
        requestBody = (rawUrlMatcher?.body ?: baseMatcher?.body).asText(),
        responseHeaders = response.headers.asText(),
        statusCode = response.statusCode,
        responseBody = response.body.asText(),
        matcherDescription = matcher.toString(),
        isCustomMatcher = baseMatcher == null && rawUrlMatcher == null,
    )
}

private fun HttpMockEditorMock.searchText(): String = listOfNotNull(
    urlText,
    method?.name ?: "ANY",
    requestHeaders,
    requestBody,
    responseHeaders,
    statusCode.toString(),
    responseBody,
    matcherDescription,
).joinToString(separator = " ").lowercase()

private fun Headers?.asText(): String? = this?.values
    ?.flatMap { (name, values) -> values.map { value -> "$name: $value" } }
    ?.joinToString(separator = "\n")

private fun HttpBody?.asText(): String? = when (this) {
    null -> null
    is JsonBody -> body
    else -> toString()
}

private fun HttpUrl.asDescription(): String = buildList {
    scheme?.let { add("scheme=$it") }
    host?.let { add("host=$it") }
    port?.let { add("port=$it") }
    path?.let { add("path=$it") }
    queryParameters.entries.sortedBy { it.key }.forEach { (name, values) ->
        add(values.sorted().joinToString(prefix = "query[$name]=[", postfix = "]"))
    }
}.joinToString(separator = " ")

private val HTTP_STATUS_CODE_RANGE = 100..599
