package dag.khinkal.molapi.room

import dag.khinkal.molapi.core.matcher.ApiRequestMatcher
import dag.khinkal.molapi.core.model.ApiMock
import dag.khinkal.molapi.core.model.ApiRequest
import dag.khinkal.molapi.core.model.ApiResponse
import dag.khinkal.molapi.core.registry.ApiMockRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking

public class RoomApiMockRegistry<
        Request : ApiRequest,
        Matcher : ApiRequestMatcher<Request>,
        Response : ApiResponse
        > internal constructor(
    private val storage: ApiMockStorage,
    private val parser: ApiMockParser<Request, Matcher, Response>,
    private val coroutineScope: CoroutineScope,
) : ApiMockRegistry<Request, Matcher, Response> {

    init {
        storage.observeAll()
            .map { it.map(parser::decodeRecord) }
            .distinctUntilChanged()
            .onEach { mocks ->
                _mocks.update { mocks }
            }
            .launchIn(coroutineScope)
    }

    private val _mocks =
        MutableStateFlow<List<ApiMock<Request, Matcher, Response>>>(emptyList())
    override val mocks: StateFlow<List<ApiMock<Request, Matcher, Response>>> = _mocks.asStateFlow()

    public constructor(
        database: MolApiRoomDatabase,
        parser: ApiMockParser<Request, Matcher, Response>,
        coroutineScope: CoroutineScope,
    ) : this(
        storage = DatabaseApiMockStorage(database.apiMockDao()),
        parser = parser,
        coroutineScope = coroutineScope,
    )

    public override fun add(mock: ApiMock<Request, Matcher, Response>) {
        val record = encode(mock)

        storage.add(record)
    }

    override fun remove(mock: ApiMock<Request, Matcher, Response>): Boolean {
        val matcher = parser.encodeMatcher(mock.matcher)
        val response = parser.encodeResponse(mock.response)

        return storage.remove(
            response = response,
            matcher = matcher,
        )
    }

    public override fun clear() {
        storage.clear()
    }

    public override fun find(request: Request): ApiMock<Request, Matcher, Response>? = runBlocking {
        val mocks = storage.observeAll()
            .map { it.map(parser::decodeRecord) }
            .first()

        mocks.firstOrNull { mock -> mock.matcher.matches(request) }
    }

    private fun encode(mock: ApiMock<Request, Matcher, Response>): RoomApiMockRecord =
        RoomApiMockRecord(
            id = mock.hashCode().toString(),
            matcher = parser.encodeMatcher(mock.matcher),
            response = parser.encodeResponse(mock.response),
        )
}
