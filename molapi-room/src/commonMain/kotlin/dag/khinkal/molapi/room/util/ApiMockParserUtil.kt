package dag.khinkal.molapi.room.util

import dag.khinkal.molapi.core.matcher.ApiRequestMatcher
import dag.khinkal.molapi.core.model.ApiMock
import dag.khinkal.molapi.core.model.ApiRequest
import dag.khinkal.molapi.core.model.ApiResponse
import dag.khinkal.molapi.core.model.BaseApiMock
import dag.khinkal.molapi.room.ApiMockParser
import dag.khinkal.molapi.room.RoomApiMockRecord

internal fun <
        Request : ApiRequest,
        Matcher : ApiRequestMatcher<Request>,
        Response : ApiResponse,
        > ApiMockParser<Request, Matcher, Response>.decodeRecord(
    mock: RoomApiMockRecord,
): ApiMock<Request, Matcher, Response> = BaseApiMock(
    id = mock.id,
    matcher = decodeMatcher(mock.matcher),
    response = decodeResponse(mock.response)
)