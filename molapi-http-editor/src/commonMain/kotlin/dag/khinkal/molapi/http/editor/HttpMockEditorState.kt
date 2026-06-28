package dag.khinkal.molapi.http.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dag.khinkal.molapi.core.matcher.ApiRequestMatcher
import dag.khinkal.molapi.core.model.ApiMock
import dag.khinkal.molapi.http.matcher.BaseHttpRequestMatcher
import dag.khinkal.molapi.http.model.Headers
import dag.khinkal.molapi.http.model.HttpBody
import dag.khinkal.molapi.http.model.HttpMethod
import dag.khinkal.molapi.http.model.HttpRequest
import dag.khinkal.molapi.http.model.HttpResponse
import dag.khinkal.molapi.http.model.JsonBody
import dag.khinkal.molapi.http.registry.HttpApiMockRegistry

internal data class HttpMockEditorMock(
    val id: Any,
    val url: String?,
    val method: HttpMethod?,
    val requestHeaders: String?,
    val requestBody: String?,
    val responseHeaders: String?,
    val statusCode: Int,
    val responseBody: String?,
    val matcherDescription: String,
)

internal sealed interface HttpMockEditorDraftError {
    data object StatusCodeMustBeNumber : HttpMockEditorDraftError
    data class InvalidHeaderLine(val lineNumber: Int) : HttpMockEditorDraftError
}

internal class HttpMockEditorState(
    private val registry: HttpApiMockRegistry,
) {

    var searchQuery: String by mutableStateOf("")

    var draftMatcherUrl: String by mutableStateOf("")

    var draftMatcherMethod: HttpMethod? by mutableStateOf(HttpMethod.GET)

    var draftMatcherHeaders: String by mutableStateOf("")

    var draftMatcherBody: String by mutableStateOf("")

    var draftResponseStatusCode: String by mutableStateOf("200")

    var draftResponseHeaders: String by mutableStateOf("")

    var draftResponseBody: String by mutableStateOf("")

    var draftError: HttpMockEditorDraftError? by mutableStateOf(null)
        private set

    val hasDraftError: Boolean
        get() = draftError != null

    val visibleMocks: List<HttpMockEditorMock>
        get() = filterMocks(registry.mocks.value)

    @Composable
    fun collectVisibleMocks(): List<HttpMockEditorMock> {
        val mocks by registry.mocks.collectAsStateWithLifecycle()
        return filterMocks(mocks)
    }

    private fun filterMocks(
        mocks: List<ApiMock<HttpRequest, ApiRequestMatcher<HttpRequest>, HttpResponse, *>>,
    ): List<HttpMockEditorMock> {
        val queryTokens = searchQuery
            .trim()
            .lowercase()
            .split(Regex("\\s+"))
            .filter { token -> token.isNotEmpty() }

        return mocks
            .map { mock -> mock.toEditorMock() }
            .filter { mock ->
                queryTokens.isEmpty() || queryTokens.all { token ->
                    mock.searchText().contains(token)
                }
            }
    }

    fun removeMock(
        mock: HttpMockEditorMock,
    ): Boolean {
        return registry.remove(mock.id)
    }

    fun clearMocks() {
        registry.clear()
    }

    fun addDraftMock(): Boolean {

        val statusCode = draftResponseStatusCode.trim().toIntOrNull()
        if (statusCode == null) {
            draftError = HttpMockEditorDraftError.StatusCodeMustBeNumber
            return false
        }

        val matcherHeaders = if (draftMatcherHeaders.isBlank()) {
            null
        } else {
            parseHeaders(draftMatcherHeaders) ?: return false
        }
        val responseHeaders = if (draftResponseHeaders.isBlank()) {
            null
        } else {
            parseHeaders(draftResponseHeaders) ?: return false
        }
        val requestBody =
            draftMatcherBody.trim().takeIf { body -> body.isNotEmpty() }?.let(::JsonBody)
        val responseBody = draftResponseBody
            .takeIf { body -> body.isNotBlank() }
            ?.let(::JsonBody)
        registry.add(
            matcher = BaseHttpRequestMatcher(
                url = draftMatcherUrl.trim().takeIf { url -> url.isNotEmpty() },
                method = draftMatcherMethod,
                body = requestBody,
                headers = matcherHeaders,
            ),
            response = HttpResponse(
                body = responseBody,
                headers = responseHeaders,
                statusCode = statusCode,
            ),
        )
        resetDraft()
        return true
    }

    private fun resetDraft() {
        draftMatcherUrl = ""
        draftMatcherMethod = HttpMethod.GET
        draftMatcherHeaders = ""
        draftMatcherBody = ""
        draftResponseStatusCode = "200"
        draftResponseHeaders = ""
        draftResponseBody = ""
        draftError = null
    }

    private fun parseHeaders(value: String): Headers? {
        val headerValues = mutableMapOf<String, MutableSet<String>>()
        value.lineSequence()
            .map { line -> line.trim() }
            .filter { line -> line.isNotEmpty() }
            .forEachIndexed { index, line ->
                val separatorIndex = line.indexOf(':')
                if (separatorIndex <= 0) {
                    draftError = HttpMockEditorDraftError.InvalidHeaderLine(index + 1)
                    return null
                }
                val name = line.substring(startIndex = 0, endIndex = separatorIndex).trim()
                val headerValue = line.substring(startIndex = separatorIndex + 1).trim()
                if (name.isEmpty() || headerValue.isEmpty()) {
                    draftError = HttpMockEditorDraftError.InvalidHeaderLine(index + 1)
                    return null
                }
                headerValues.getOrPut(name) { linkedSetOf() }.add(headerValue)
            }

        draftError = null
        return headerValues
            .takeIf { headers -> headers.isNotEmpty() }
            ?.mapValues { entry -> entry.value.toSet() }
            ?.let(::Headers)
    }
}

private fun ApiMock<HttpRequest, *, HttpResponse, *>.toEditorMock(): HttpMockEditorMock {
    val baseMatcher = matcher as? BaseHttpRequestMatcher
    val httpResponse = response

    return HttpMockEditorMock(
        id = id,
        url = baseMatcher?.url,
        method = baseMatcher?.method,
        requestHeaders = baseMatcher?.headers.asText(),
        requestBody = baseMatcher?.body?.asText(),
        responseHeaders = httpResponse.headers.asText(),
        statusCode = httpResponse.statusCode,
        responseBody = httpResponse.body.asText(),
        matcherDescription = matcher.toString(),
    )
}

private fun HttpMockEditorMock.searchText(): String = listOfNotNull(
    url,
    method?.name ?: "ANY",
    requestHeaders,
    requestBody,
    responseHeaders,
    statusCode.toString(),
    responseBody,
    matcherDescription,
).joinToString(separator = " ").lowercase()

private fun Headers?.asText(): String? = this?.values
    ?.flatMap { entry -> entry.value.map { value -> "${entry.key}: $value" } }
    ?.joinToString(separator = "\n")

private fun HttpBody?.asText(): String? = when (this) {
    null -> null
    is JsonBody -> body
    else -> toString()
}
