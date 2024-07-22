package pini.mattia.shopfullytest.domain.analytics

interface StreamFullyEvent {
    val eventType: String
    val attributes: Map<String, Any>
}