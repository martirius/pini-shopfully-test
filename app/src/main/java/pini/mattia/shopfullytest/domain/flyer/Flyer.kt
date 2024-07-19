package pini.mattia.shopfullytest.domain.flyer

data class Flyer(
    val id : Int,
    val retailerId: Int,
    val title: String,
    val isXL: Boolean,
    val flyerBackground: String,
    val isAlreadySeen: Boolean = false
)