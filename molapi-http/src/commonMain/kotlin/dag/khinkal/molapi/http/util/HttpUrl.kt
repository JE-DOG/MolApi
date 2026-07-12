package dag.khinkal.molapi.http.util

import dag.khinkal.molapi.http.model.HttpUrl

public fun HttpUrl.matches(actual: HttpUrl): Boolean {
    if (scheme != null && !scheme.equals(actual.scheme, ignoreCase = true)) {
        return false
    }
    if (host != null && !host.equals(actual.host, ignoreCase = true)) {
        return false
    }
    if (port != null && port != actual.port) {
        return false
    }
    if (path != null && actual.path?.contains(path) != true) {
        return false
    }
    if (queryParameters.any { (name, values) -> actual.queryParameters[name] != values }) {
        return false
    }
    return true
}

public fun HttpUrl.toRawUrl(): String = buildString {
    if (scheme != null) {
        append(scheme)
        append("://")
    }
    if (host != null) {
        append(host)
    }
    if (port != null && !isDefaultPort()) {
        append(':')
        append(port)
    }
    if (!path.isNullOrBlank()) {
        if (path.first() != '/') append('/')
        append(path)
    }
    if (queryParameters.isNotEmpty()) {
        append('?')
        append(
            queryParameters.entries.joinToString("&") { (name, values) ->
                values.joinToString("&") { value -> "$name=$value" }
            },
        )
    }
}

private fun HttpUrl.isDefaultPort(): Boolean =
    (scheme.equals("http", ignoreCase = true) && port == 80) ||
            (scheme.equals("https", ignoreCase = true) && port == 443)
