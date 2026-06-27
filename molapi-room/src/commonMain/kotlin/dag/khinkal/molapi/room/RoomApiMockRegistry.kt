package dag.khinkal.molapi.room

import dag.khinkal.molapi.core.idgenerator.ApiMockIdGenerator
import dag.khinkal.molapi.core.matcher.ApiRequestMatcher
import dag.khinkal.molapi.core.model.ApiMock
import dag.khinkal.molapi.core.model.ApiRequest
import dag.khinkal.molapi.core.model.ApiResponse
import dag.khinkal.molapi.core.model.BaseApiMock
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

// TODO: Remote runBlocking
public class RoomApiMockRegistry<
        Request : ApiRequest,
        Matcher : ApiRequestMatcher<Request>,
        Response : ApiResponse,
        Id : Any,
        > internal constructor(
    private val storage: ApiMockStorage,
    private val parser: ApiMockParser<Request, Matcher, Response, Id>,
    public override val idGenerator: ApiMockIdGenerator<Request, Matcher, Response, Id>,
    private val coroutineScope: CoroutineScope,
) : ApiMockRegistry<Request, Matcher, Response, Id> {

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
        MutableStateFlow<List<ApiMock<Request, Matcher, Response, Id>>>(emptyList())
    override val mocks: StateFlow<List<ApiMock<Request, Matcher, Response, Id>>> =
        _mocks.asStateFlow()

    public constructor(
        database: MolApiRoomDatabase,
        parser: ApiMockParser<Request, Matcher, Response, Id>,
        idGenerator: ApiMockIdGenerator<Request, Matcher, Response, Id>,
        coroutineScope: CoroutineScope,
    ) : this(
        storage = DatabaseApiMockStorage(database.apiMockDao()),
        parser = parser,
        idGenerator = idGenerator,
        coroutineScope = coroutineScope,
    )

    public override fun add(
        response: Response,
        matcher: Matcher
    ) {
        val mock = BaseApiMock(
            id = idGenerator.generateId(matcher = matcher, response = response),
            matcher = matcher,
            response = response,
        )
        val record = encode(mock)

        storage.add(record)
    }

    public override fun remove(id: Id): Boolean {
        return storage.remove(id.toString())
    }

    public override fun clear() {
        storage.clear()
    }

    public override fun find(request: Request): ApiMock<Request, Matcher, Response, Id>? =
        runBlocking {
            val mocks = storage.observeAll()
                .map { it.map(parser::decodeRecord) }
                .first()

            mocks.firstOrNull { mock -> mock.matcher.matches(request) }
        }

    private fun encode(mock: ApiMock<Request, Matcher, Response, Id>): RoomApiMockRecord =
        RoomApiMockRecord(
            id = mock.id.toString(),
            matcher = parser.encodeMatcher(mock.matcher),
            response = parser.encodeResponse(mock.response),
        )
}
