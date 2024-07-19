package pini.mattia.shopfullytest.domain.error

sealed interface FlyerError {
    data object NoFlyer: FlyerError

    class FlyerGetError(val error: NetworkError): FlyerError
}

sealed interface NetworkError {
    data object NoConnection: NetworkError

    data object ConnectionTimeout: NetworkError

    data object ServerDown: NetworkError
}