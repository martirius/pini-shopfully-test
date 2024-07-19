package pini.mattia.shopfullytest.domain.flyer

interface FlyerRepository {
    suspend fun getFlyers(): List<Flyer>
}