package dag.khinkal.molapi.http.editor

import dag.khinkal.molapi.http.model.HttpMethod
import dag.khinkal.molapi.http.model.HttpRequest
import dag.khinkal.molapi.http.model.JsonBody
import dag.khinkal.molapi.http.registry.HttpInMemoryApiMockRegistry
import kotlin.test.Test
import kotlin.test.assertEquals
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
                url = "/tasks",
                method = HttpMethod.POST,
                body = JsonBody("""{"title":"new"}"""),
            ),
        )
        assertNotNull(mock)
        assertEquals(201, mock.response.statusCode)
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
