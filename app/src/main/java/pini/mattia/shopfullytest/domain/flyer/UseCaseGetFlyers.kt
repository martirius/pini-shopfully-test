package pini.mattia.shopfullytest.domain.flyer

import arrow.core.Either
import pini.mattia.shopfullytest.domain.error.FlyerError
import javax.inject.Inject

class UseCaseGetFlyers @Inject constructor(
    private val flyerRepository: FlyerRepository
) {
    suspend fun execute(): Either<FlyerError, List<Flyer>> = flyerRepository.getFlyers()
}