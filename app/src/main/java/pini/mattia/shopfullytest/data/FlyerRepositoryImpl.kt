package pini.mattia.shopfullytest.data

import arrow.core.Either
import io.ktor.client.HttpClient
import pini.mattia.shopfullytest.domain.error.FlyerError
import pini.mattia.shopfullytest.domain.flyer.Flyer
import pini.mattia.shopfullytest.domain.flyer.FlyerRepository
import javax.inject.Inject

class FlyerRepositoryImpl @Inject constructor(private val flyerMapper: FlyerMapper,private val ktorClient: HttpClient): FlyerRepository {
    override suspend fun getFlyers(): Either<FlyerError, List<Flyer>> {
        TODO()
    }
}