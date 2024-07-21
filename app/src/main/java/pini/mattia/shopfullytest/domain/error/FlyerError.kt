package pini.mattia.shopfullytest.domain.error

sealed interface FlyerError {
    data object NoFlyer: FlyerError

    data class ApiError(val error: NetworkError): FlyerError

    data class SerializationError(val message: String): FlyerError

}

sealed interface NetworkError {
    data object NoConnection: NetworkError

    data object ConnectionTimeout: NetworkError

    data object ServerError: NetworkError

}