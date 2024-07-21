package pini.mattia.shopfullytest

import arrow.core.getOrElse
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Test
import pini.mattia.shopfullytest.data.FlyerMapper
import pini.mattia.shopfullytest.data.FlyerRepositoryImpl
import pini.mattia.shopfullytest.domain.error.FlyerError
import pini.mattia.shopfullytest.domain.error.NetworkError
import java.io.IOException
import java.net.SocketTimeoutException

private const val TEST_RESPONSE = """{
    "metadata": {
        "success": 1,
        "code": 200,
        "message": "OK",
        "time": 0.09
    },
    "data": [
        {
            "Flyer": {
                "id": 1199019,
                "retailer_id": 23,
                "title": "Offerte d'estate",
                "is_xl": false
            }
        },
        {
            "Flyer": {
                "id": 1196636,
                "retailer_id": 3735,
                "title": "Pan Piattini",
                "is_xl": false
            }
        }
    ]
}"""

private const val BROKEN_TEST_RESPONSE = """{
    "metadata": {
        "success": 1,
        "code": 200,
        "message": "OK",
        "time": 0.09
    },
    "data": [
        {
            "Flyer": {
                "id": 1199019,
                "retailer_id": 23,
                "title": "Offerte d'estate",
                "is_xl": false
            }
        },
        {
            "Flyer": {
                "id": 1196636,
                "retailer_id": 3735,
                "title": "Pan Piattini",
                "is_xl": false
            }
        },
    ]
}"""

private const val NO_FLYERS_TEST_RESPONSE = """{
    "metadata": {
        "success": 1,
        "code": 200,
        "message": "OK",
        "time": 0.09
    },
    "data": []
}"""


class FlyerRepositoryTest {

    private val flyerMapper = FlyerMapper()

    @Test
    fun when_response_successful_flyers_are_present() = runTest {

        val mockEngine = MockEngine { _ ->
            respond(
                content = ByteReadChannel(TEST_RESPONSE),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val mockHttpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }

        val flyerRepository = FlyerRepositoryImpl(flyerMapper, mockHttpClient)

        val result = flyerRepository.getFlyers()

        assertTrue(result.isRight())
        val flyers = result.getOrElse { emptyList() }
        assertFalse(flyers.isEmpty())
        assertEquals(2, flyers.size)
        //now we just check if the flyers ID are correct
        assertEquals(1199019, flyers.first().id)
        assertEquals(1196636, flyers[1].id)
    }

    @Test
    fun when_json_is_bad_result_is_FlyerError_SerializationError() = runTest {

        val mockEngine = MockEngine { _ ->
            respond(
                content = ByteReadChannel(BROKEN_TEST_RESPONSE),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val mockHttpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }

        val flyerRepository = FlyerRepositoryImpl(flyerMapper, mockHttpClient)

        val result = flyerRepository.getFlyers()

        assertTrue(result.isLeft())
        result.fold(
            ifLeft = { error ->
                assertTrue(error is FlyerError.SerializationError)
            },
            ifRight = {
                //none because is not right
            }
        )

    }

    @Test
    fun when_IOException_result_is_FlyerError_GetError_NetworkError() = runTest {

        val mockEngine = MockEngine { _ ->
            throw IOException()
        }
        val mockHttpClient = HttpClient(mockEngine) {
        }

        val flyerRepository = FlyerRepositoryImpl(flyerMapper, mockHttpClient)
        val result = flyerRepository.getFlyers()

        assertTrue(result.isLeft())
        result.fold(
            ifLeft = { error ->
                assert(error is FlyerError.ApiError)
                assertTrue((error as FlyerError.ApiError).error is NetworkError.NoConnection)
            },
            ifRight = {
                //none because is not right
            }
        )
    }

    @Test
    fun when_SocketTimeoutException_result_is_FlyerError_GetError_ConnectionTimeout() = runTest {

        val mockEngine = MockEngine { _ ->
            throw SocketTimeoutException()
        }
        val mockHttpClient = HttpClient(mockEngine) {
        }

        val flyerRepository = FlyerRepositoryImpl(flyerMapper, mockHttpClient)
        val result = flyerRepository.getFlyers()

        assertTrue(result.isLeft())
        result.fold(
            ifLeft = { error ->
                assert(error is FlyerError.ApiError)
                assertTrue((error as FlyerError.ApiError).error is NetworkError.ConnectionTimeout)
            },
            ifRight = {
                //none because is not right
            }
        )

    }

    @Test
    fun when_server_response_broken_result_is_FlyerError_GetError_ServerError() = runTest {

        val mockEngine = MockEngine { _ ->
            respond(
                content = ByteReadChannel("Server error"),
                status = HttpStatusCode.InternalServerError,
                headers = headersOf(HttpHeaders.ContentType, "*/*")
            )
        }
        val mockHttpClient = HttpClient(mockEngine) {
        }

        val flyerRepository = FlyerRepositoryImpl(flyerMapper, mockHttpClient)
        val result = flyerRepository.getFlyers()

        assertTrue(result.isLeft())
        result.fold(
            ifLeft = { error ->
                assert(error is FlyerError.ApiError)
                assertTrue((error as FlyerError.ApiError).error is NetworkError.ServerError)
            },
            ifRight = {
                //none because is not right
            }
        )
    }

    @Test
    fun when_response_successful_and_flyers_not_present_then_FlyerError_NoFlyers() = runTest {

        val mockEngine = MockEngine { _ ->
            respond(
                content = ByteReadChannel(NO_FLYERS_TEST_RESPONSE),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val mockHttpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }

        val flyerRepository = FlyerRepositoryImpl(flyerMapper, mockHttpClient)

        val result = flyerRepository.getFlyers()

        assertTrue(result.isLeft())
        result.fold(
            ifLeft = { error ->
                assertTrue(error is FlyerError.NoFlyer)
            },
            ifRight = {
                //none because is not right
            }
        )
    }
}