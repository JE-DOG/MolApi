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
import io.ktor.client.request.HttpSendPipeline
import io.ktor.client.request.setBody
import io.ktor.util.AttributeKey
import io.ktor.util.pipeline.PipelinePhase

public class MolApiKtorPluginConfig {

    public var registry: ReadApiMockRegistry<
            HttpRequest,
            ApiRequestMatcher<HttpRequest>,
            HttpResponse,
            >? = null
}

private val MolApiMockResponseAttribute: AttributeKey<HttpResponse> =
    AttributeKey("MolApiMockResponse")

private val MolApiMockResponsePhase: PipelinePhase =
    PipelinePhase("MolApiMockResponse")

public val MolApiKtorPlugin: ClientPlugin<MolApiKtorPluginConfig> = createClientPlugin(
    name = "MolApiKtorPlugin",
    createConfiguration = ::MolApiKtorPluginConfig
) {

    val registry = pluginConfig.registry
        ?: error("MolApiKtorPlugin requires registry")
    val httpClient = client

    httpClient.sendPipeline.insertPhaseBefore(
        HttpSendPipeline.Engine,
        MolApiMockResponsePhase,
    )
    httpClient.sendPipeline.intercept(MolApiMockResponsePhase) { content ->
        val response = context.attributes.getOrNull(MolApiMockResponseAttribute)
        if (response == null) {
            proceed()
            return@intercept
        }

        val requestData = context.apply {
            setBody(content)
        }.build()
        val call = response.toKtorHttpClientCall(
            client = httpClient,
            requestData = requestData,
            callContext = context.executionContext,
        )
        val processedResponse = httpClient.receivePipeline.execute(Unit, call.response)

        finish()
        proceedWith(processedResponse.call)
    }

    on(Send) { request ->
        val molApiRequest = request.toMolApiHttpRequestOrNull()
        val mock = molApiRequest?.let(registry::find)
            ?: return@on proceed(request)

        request.attributes.put(MolApiMockResponseAttribute, mock.response)
        proceed(request)
    }
}
