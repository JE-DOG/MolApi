package dag.khinkal.molapi.http.editor

import dag.khinkal.molapi.http.registry.HttpInMemoryApiMockRegistry
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertTrue

class MolApiEditorTest {

    @AfterTest
    fun tearDown() {
        MolApiEditor.resetForTests()
    }

    @Test
    fun registryReturnsInitializedRegistry() {
        val registry = HttpInMemoryApiMockRegistry()

        MolApiEditor.init(registry)

        assertSame(registry, MolApiEditor.registry)
    }

    @Test
    fun registryFailsWhenEditorIsNotInitialized() {
        val error = assertFailsWith<IllegalStateException> {
            MolApiEditor.registry
        }

        assertTrue(error.message.orEmpty().contains("MolApiEditor.init"))
    }
}
