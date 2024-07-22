import pini.mattia.shopfullytest.domain.analytics.StreamFullyEvent

sealed interface StreamFullyEvents : StreamFullyEvent {
    fun buildAttributes(): Map<String, Any>
    data class FlyerOpen(
        val retailerId: Int,
        val flyerId: Int,
        val title: String,
        val position: Int,
        val firstRead: Boolean
    ) : StreamFullyEvents {
        override fun buildAttributes(): Map<String, Any> = mapOf(
            "retailer_id" to retailerId,
            "flyer_id" to flyerId,
            "title" to title,
            "position" to position,
            "first_read" to firstRead
        )

        override val eventType: String
            get() = "flyer_open"
        override val attributes: Map<String, Any>
            get() = buildAttributes()
    }

    data class FlyerSession(val flyerId: Int, val sessionDuration: Int, val firstRead: Boolean) :
        StreamFullyEvents {
        override fun buildAttributes(): Map<String, Any> = mapOf(
            "flyer_id" to flyerId,
            "session_duration" to sessionDuration,
            "first_read" to firstRead
        )

        override val eventType: String
            get() = "flyer_session"
        override val attributes: Map<String, Any>
            get() = buildAttributes()

    }

    data class Viewability(val flyerId: Int, val duration: Int, val percentage: Int): StreamFullyEvents {
        override fun buildAttributes(): Map<String, Any> = mapOf(
            "flyer_id" to flyerId,
            "duration" to duration,
            "percentage" to percentage
        )

        override val eventType: String
            get() = "viewability"
        override val attributes: Map<String, Any>
            get() = buildAttributes()

    }
}