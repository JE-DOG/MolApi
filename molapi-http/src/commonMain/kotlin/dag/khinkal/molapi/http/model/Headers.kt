package dag.khinkal.molapi.http.model

public data class Headers(
    public val values: Map<String, Set<String>>
) {

    public companion object {

        public fun empty(): Headers = Headers(emptyMap())

        public fun jsonContent(): Headers = Headers(
            mapOf("Content-Type" to setOf("application/json")),
        )
    }
}
