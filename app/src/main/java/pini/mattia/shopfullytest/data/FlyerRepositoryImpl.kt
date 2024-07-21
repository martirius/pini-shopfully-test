package pini.mattia.shopfullytest.data

import arrow.core.Either
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.serialization.JsonConvertException
import pini.mattia.shopfullytest.domain.error.FlyerError
import pini.mattia.shopfullytest.domain.error.NetworkError
import pini.mattia.shopfullytest.domain.flyer.Flyer
import pini.mattia.shopfullytest.domain.flyer.FlyerRepository
import java.lang.Exception
import java.net.SocketTimeoutException
import javax.inject.Inject

class FlyerRepositoryImpl @Inject constructor(
    private val flyerMapper: FlyerMapper,
    private val ktorClient: HttpClient
) : FlyerRepository {
    override suspend fun getFlyers(): Either<FlyerError, List<Flyer>> {
        return try {
            val response =
                ktorClient.get("https://run.mocky.io/v3/3eff005c-3c8c-45cc-84fb-3bd1c0b4540d")
            if (response.status.value in 200..299) {
                val apiCallBody = response.body<ApiCallResponse>()
                val flyers = apiCallBody.data.map { flyerWrapper ->
                    flyerMapper.map(flyerWrapper.flyer)
                }
                if (flyers.isEmpty()) Either.Left(FlyerError.NoFlyer) else Either.Right(flyers)
            } else {
                Either.Left(FlyerError.ApiError(NetworkError.ServerError))
            }
        } catch (e: Exception) {
            Either.Left(mapAPiCallException(e))
        }
    }

    private fun mapAPiCallException(exception: Exception): FlyerError {
        return when (exception) {
            is SocketTimeoutException -> {
                FlyerError.ApiError(NetworkError.ConnectionTimeout)
            }

            is ServerResponseException -> {
                FlyerError.ApiError(NetworkError.ServerError)
            }

            is JsonConvertException -> {
                FlyerError.SerializationError(exception.message ?: "De-serialization error")
            }

            else -> {
                FlyerError.ApiError(NetworkError.NoConnection)
            }
        }
    }
}