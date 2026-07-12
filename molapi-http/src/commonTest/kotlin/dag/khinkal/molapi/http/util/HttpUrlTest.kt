package dag.khinkal.molapi.http.util

import dag.khinkal.molapi.http.model.HttpUrl
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HttpUrlTest {

    @Test
    fun convertsStructuredUrlWithDefaultPortAndQueryParameters() {
        val url = HttpUrl(
            scheme = "https",
            host = "example.com",
            port = 443,
            path = "/tasks",
            queryParameters = linkedMapOf(
                "status" to listOf("active", "paused"),
                "page" to listOf("2"),
            ),
        )

        assertEquals(
            "https://example.com/tasks?status=active&status=paused&page=2",
            url.toRawUrl(),
        )
    }

    @Test
    fun convertsStructuredUrlWithDefaultPortAndQueryParametersWithoutStartSlashInPath() {
        val url = HttpUrl(
            scheme = "https",
            host = "example.com",
            port = 443,
            path = "tasks",
            queryParameters = linkedMapOf(
                "status" to listOf("active", "paused"),
                "page" to listOf("2"),
            ),
        )

        assertEquals(
            "https://example.com/tasks?status=active&status=paused&page=2",
            url.toRawUrl(),
        )
    }

    @Test
    fun convertsStructuredUrlWithNonDefaultPortAndQueryParameters() {
        val url = HttpUrl(
            scheme = "https",
            host = "example.com",
            port = 80,
            path = "/tasks",
            queryParameters = linkedMapOf(
                "status" to listOf("active", "paused"),
                "page" to listOf("2"),
            ),
        )

        assertEquals(
            "https://example.com:80/tasks?status=active&status=paused&page=2",
            url.toRawUrl(),
        )
    }

    @Test
    fun keepsNonDefaultPort() {
        val url = HttpUrl(
            scheme = "https",
            host = "example.com",
            port = 8443,
            path = "/tasks",
        )

        assertEquals("https://example.com:8443/tasks", url.toRawUrl())
    }

    @Test
    fun convertsPathOnlyUrl() {
        val url = HttpUrl(path = "/tasks/42")

        assertEquals("/tasks/42", url.toRawUrl())
    }

    @Test
    fun convertsEmptyUrlToEmptyString() {
        assertEquals("", HttpUrl().toRawUrl())
    }

    @Test
    fun matchesConfiguredComponentsAndPathPart() {
        val expected = HttpUrl(
            scheme = "HTTPS",
            host = "EXAMPLE.COM",
            port = 443,
            path = "/tasks/",
            queryParameters = mapOf("status" to listOf("active")),
        )
        val actual = HttpUrl(
            scheme = "https",
            host = "example.com",
            port = 443,
            path = "/tasks/42",
            queryParameters = mapOf(
                "status" to listOf("active"),
                "tracking" to listOf("enabled"),
            ),
        )

        assertTrue(expected.matches(actual))
    }

    @Test
    fun doesNotMatchDifferentConfiguredComponent() {
        val expected = HttpUrl(host = "example.com")
        val actual = HttpUrl(host = "other.com")

        assertFalse(expected.matches(actual))
    }
}
