package pini.mattia.shopfullytest.domain.flyer

data class Flyer(
    val id : String,
    val retailerId: String,
    val title: String,
    val isXL: Boolean,
    val flyerBackground: String,
    val isAlreadySeen: Boolean = false
)