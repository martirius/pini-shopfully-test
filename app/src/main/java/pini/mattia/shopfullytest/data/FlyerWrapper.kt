package pini.mattia.shopfullytest.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FlyerWrapper(
    @SerialName("Flyer")
    val flyer: FlyerDTO
)