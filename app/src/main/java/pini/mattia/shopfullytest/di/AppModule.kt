package pini.mattia.shopfullytest.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import pini.mattia.shopfullytest.data.FlyerRepositoryImpl
import pini.mattia.shopfullytest.domain.analytics.StreamFully
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


    @Provides
    @Singleton
    fun getStreamFully(@ApplicationContext context: Context): StreamFully = StreamFully(
        context.packageName,
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    )

}

@Module
@InstallIn(SingletonComponent::class)
interface BindsModule {
    @Binds
    @Singleton
    fun provideFlyerRepository(impl: FlyerRepositoryImpl): FlyerRepository
}