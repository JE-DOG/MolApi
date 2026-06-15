package dag.khinkal.molapi.http.ktor.plugin

import dag.khinkal.molapi.core.matcher.ApiRequestMatcher
import dag.khinkal.molapi.core.registry.ReadApiMockRegistry
import dag.khinkal.molapi.http.ktor.util.toKtorHttpClientCall
import dag.khinkal.molapi.http.ktor.util.toMolApiHttpRequestOrNull
import dag.khinkal.molapi.http.model.HttpRequest
import dag.khinkal.molapi.http.model.HttpResponse
import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin

public class MolApiKtorPluginConfig {

    public var registry: ReadApiMockRegistry<
            HttpRequest,
            ApiRequestMatcher<HttpRequest>,
            HttpResponse
            >? = null
}

public val MolApiKtorPlugin: ClientPlugin<MolApiKtorPluginConfig> = createClientPlugin(
    name = "MolApiKtorPlugin",
    createConfiguration = ::MolApiKtorPluginConfig
) {
    val registry = pluginConfig.registry
        ?: error("MolApiKtorPlugin requires registry")

    on(Send) { request ->
        val molApiRequest = request.toMolApiHttpRequestOrNull()
        val mock = molApiRequest?.let(registry::find)
            ?: return@on proceed(request)

        mock.response.toKtorHttpClientCall(
            client = client,
            requestData = request.build(),
            callContext = coroutineContext
        )
    }
}
