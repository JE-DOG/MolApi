package dag.khinkal.molapi.http.editor

import dag.khinkal.molapi.http.matcher.RawHttpUrlRequestMatcher
import dag.khinkal.molapi.http.model.HttpMethod
import dag.khinkal.molapi.http.model.HttpRequest
import dag.khinkal.molapi.http.model.HttpUrl
import dag.khinkal.molapi.http.model.JsonBody
import dag.khinkal.molapi.http.registry.HttpInMemoryApiMockRegistry
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class HttpMockEditorViewModelTest {

    @Test
    fun addDraftMockUsesResponseBodyLoadedFromDocument() {
        val registry = HttpInMemoryApiMockRegistry()
        val viewModel = HttpMockEditorViewModel(registry = registry)

        viewModel.onDraftMatcherUrlChanged("/tasks/42")
        viewModel.onDraftResponseBodyDocumentSelected("""{"id":42}""")

        assertTrue(viewModel.addDraftMock())

        val mock = registry.mocks.value.single()
        val matcher = assertIs<RawHttpUrlRequestMatcher>(mock.matcher)
        assertEquals("/tasks/42", matcher.rawUrl)
        assertEquals(JsonBody("""{"id":42}"""), mock.response.body)
        assertEquals("", viewModel.draft.responseBody)
    }

    @Test
    fun addDraftMockUsesMatcherBodyLoadedFromDocument() {
        val registry = HttpInMemoryApiMockRegistry()
        val viewModel = HttpMockEditorViewModel(registry = registry)

        viewModel.onDraftMatcherUrlChanged("/tasks")
        viewModel.onDraftMatcherMethodChanged(HttpMethod.POST)
        viewModel.onDraftMatcherBodyDocumentSelected("""{"title":"new"}""")
        viewModel.onDraftResponseStatusCodeChanged("201")

        assertTrue(viewModel.addDraftMock())

        val mock = registry.find(
            HttpRequest(
                url = HttpUrl(path = "/tasks"),
                method = HttpMethod.POST,
                body = JsonBody("""{"title":"new"}"""),
            ),
        )
        assertNotNull(mock)
        assertEquals(201, mock.response.statusCode)
    }

    @Test
    fun addDraftMockMatchesTextAgainstFullRequestUrl() {
        val registry = HttpInMemoryApiMockRegistry()
        val viewModel = HttpMockEditorViewModel(registry = registry)

        viewModel.onDraftMatcherUrlChanged("example.com/tasks")

        assertTrue(viewModel.addDraftMock())
        assertNotNull(
            registry.find(
                HttpRequest(
                    url = HttpUrl(
                        scheme = "https",
                        host = "example.com",
                        port = 443,
                        path = "/tasks/42",
                    ),
                    method = HttpMethod.GET,
                ),
            ),
        )
    }

    @Test
    fun setJsonDocumentReadFailedShowsDraftError() {
        val registry = HttpInMemoryApiMockRegistry()
        val viewModel = HttpMockEditorViewModel(registry = registry)

        viewModel.onJsonDocumentReadFailed()

        assertEquals(HttpMockEditorDraftError.JsonDocumentReadFailed, viewModel.draft.error)
        assertTrue(registry.mocks.value.isEmpty())
    }

    @Test
    fun keepsDraftAndDestinationInViewModel() {
        val viewModel = HttpMockEditorViewModel(HttpInMemoryApiMockRegistry())

        viewModel.onDraftMatcherUrlChanged("/tasks")
        viewModel.showCreateMock()

        assertEquals(HttpMockEditorDestination.Create, viewModel.destination)
        assertEquals("/tasks", viewModel.draft.matcherUrl)
    }

    @Test
    fun addDraftMockRejectsOutOfRangeStatusCode() {
        val viewModel = HttpMockEditorViewModel(HttpInMemoryApiMockRegistry())

        viewModel.onDraftResponseStatusCodeChanged("99")

        assertFalse(viewModel.addDraftMock())
        assertEquals(HttpMockEditorDraftError.StatusCodeOutOfRange, viewModel.draft.error)
    }

    @Test
    fun addDraftMockIdentifiesInvalidMatcherHeader() {
        val viewModel = HttpMockEditorViewModel(HttpInMemoryApiMockRegistry())

        viewModel.onDraftMatcherHeadersChanged("Missing separator")

        assertFalse(viewModel.addDraftMock())
        assertEquals(
            HttpMockEditorDraftError.InvalidMatcherHeaderLine(lineNumber = 1),
            viewModel.draft.error,
        )
    }

    @Test
    fun addDraftMockIdentifiesInvalidResponseHeader() {
        val viewModel = HttpMockEditorViewModel(HttpInMemoryApiMockRegistry())

        viewModel.onDraftResponseHeadersChanged("Content-Type")

        assertFalse(viewModel.addDraftMock())
        assertEquals(
            HttpMockEditorDraftError.InvalidResponseHeaderLine(lineNumber = 1),
            viewModel.draft.error,
        )
    }
}
