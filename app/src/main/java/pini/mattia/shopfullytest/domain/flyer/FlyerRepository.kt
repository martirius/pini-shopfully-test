package pini.mattia.shopfullytest.domain.flyer

import arrow.core.Either
import pini.mattia.shopfullytest.domain.error.FlyerError

interface FlyerRepository {
    suspend fun getFlyers(): Either<FlyerError, List<Flyer>>
}