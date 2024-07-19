package pini.mattia.shopfullytest.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FlyerDTO(
    val id: Int,
    @SerialName("retailer_id")
    val retailerId: Int,
    val title: String,
    @SerialName("is_xl")
    val isXL: Boolean
)