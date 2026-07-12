package dag.khinkal.molapi.http.editor

import dag.khinkal.molapi.http.matcher.RawHttpUrlRequestMatcher
import dag.khinkal.molapi.http.model.HttpMethod
import dag.khinkal.molapi.http.model.HttpRequest
import dag.khinkal.molapi.http.model.HttpUrl
import dag.khinkal.molapi.http.model.JsonBody
import dag.khinkal.molapi.http.registry.HttpInMemoryApiMockRegistry
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class HttpMockEditorStateTest {

    @Test
    fun addDraftMockUsesResponseBodyLoadedFromDocument() {
        val registry = HttpInMemoryApiMockRegistry()
        val state = HttpMockEditorState(registry = registry)

        state.draftMatcherUrl = "/tasks/42"
        state.setDraftResponseBodyFromDocument("""{"id":42}""")

        assertTrue(state.addDraftMock())

        val mock = registry.mocks.value.single()
        val matcher = assertIs<RawHttpUrlRequestMatcher>(mock.matcher)
        assertEquals("/tasks/42", matcher.rawUrl)
        assertEquals(JsonBody("""{"id":42}"""), mock.response.body)
        assertEquals("", state.draftResponseBody)
    }

    @Test
    fun addDraftMockUsesMatcherBodyLoadedFromDocument() {
        val registry = HttpInMemoryApiMockRegistry()
        val state = HttpMockEditorState(registry = registry)

        state.draftMatcherUrl = "/tasks"
        state.draftMatcherMethod = HttpMethod.POST
        state.setDraftMatcherBodyFromDocument("""{"title":"new"}""")
        state.draftResponseStatusCode = "201"

        assertTrue(state.addDraftMock())

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
        val state = HttpMockEditorState(registry = registry)

        state.draftMatcherUrl = "example.com/tasks"

        assertTrue(state.addDraftMock())
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
        val state = HttpMockEditorState(registry = registry)

        state.setJsonDocumentReadFailed()

        assertEquals(HttpMockEditorDraftError.JsonDocumentReadFailed, state.draftError)
        assertTrue(registry.mocks.value.isEmpty())
    }
}
