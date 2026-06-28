package dag.khinkal.molapi.http.assets

import android.content.Context
import android.content.res.AssetManager
import dag.khinkal.molapi.http.model.JsonBody
import java.nio.charset.Charset

public fun AssetJsonBody(
    context: Context,
    path: String,
    charset: Charset = Charsets.UTF_8,
): JsonBody = JsonBody.fromAssets(
    assetManager = context.assets,
    path = path,
    charset = charset,
)

public fun AssetJsonBody(
    assetManager: AssetManager,
    path: String,
    charset: Charset = Charsets.UTF_8,
): JsonBody = JsonBody.fromAssets(
    assetManager = assetManager,
    path = path,
    charset = charset,
)

public fun JsonBody.Companion.fromAssets(
    context: Context,
    path: String,
    charset: Charset = Charsets.UTF_8,
): JsonBody = AssetJsonBody(
    assetManager = context.assets,
    path = path,
    charset = charset,
)

public fun JsonBody.Companion.fromAssets(
    assetManager: AssetManager,
    path: String,
    charset: Charset = Charsets.UTF_8,
): JsonBody = JsonBody(
    body = assetManager.open(path).bufferedReader(charset).use { reader ->
        reader.readText()
    },
)
