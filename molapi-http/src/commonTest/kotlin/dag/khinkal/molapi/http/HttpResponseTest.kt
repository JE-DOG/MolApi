package dag.khinkal.molapi.http

import dag.khinkal.molapi.http.model.HttpResponse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class HttpResponseTest {

    @Test
    fun acceptsValidStatusCodes() {
        assertEquals(100, HttpResponse(statusCode = 100).statusCode)
        assertEquals(599, HttpResponse(statusCode = 599).statusCode)
    }

    @Test
    fun rejectsInvalidStatusCodes() {
        assertFailsWith<IllegalArgumentException> {
            HttpResponse(statusCode = -1)
        }
        assertFailsWith<IllegalArgumentException> {
            HttpResponse(statusCode = 999)
        }
    }
}
