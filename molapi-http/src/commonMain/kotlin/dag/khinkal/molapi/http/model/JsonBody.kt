package dag.khinkal.molapi.http.model

public data class JsonBody(
    public override val body: String
) : JsonHttpBody

public interface JsonHttpBody : HttpBody {

    public val body: String
}
