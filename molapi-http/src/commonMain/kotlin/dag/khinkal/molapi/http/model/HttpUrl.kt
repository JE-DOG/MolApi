package dag.khinkal.molapi.http.model

/**
 * Structured HTTP URL components used by requests and request matchers.
 *
 * Adapter-created requests use an effective [port] and an encoded [path]. A `null` component can
 * be used by a matcher to ignore that component. Empty [queryParameters] also means that query
 * parameters are not part of matching. Query parameter values preserve their original order and
 * duplicates.
 */
public data class HttpUrl(
    public val scheme: String? = null,
    public val host: String? = null,
    public val port: Int? = null,
    public val path: String? = null,
    public val queryParameters: Map<String, List<String>> = emptyMap(),
)
