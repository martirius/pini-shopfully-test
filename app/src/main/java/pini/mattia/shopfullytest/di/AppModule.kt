package pini.mattia.shopfullytest.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import pini.mattia.shopfullytest.data.FlyerRepositoryImpl
import pini.mattia.shopfullytest.domain.flyer.FlyerRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProvideModule {

    @Provides
    @Singleton
    fun getHttpClient(): HttpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

}

@Module
@InstallIn(SingletonComponent::class)
interface BindsModule {
    @Binds
    @Singleton
    fun provideFlyerRepository(impl: FlyerRepositoryImpl): FlyerRepository
}