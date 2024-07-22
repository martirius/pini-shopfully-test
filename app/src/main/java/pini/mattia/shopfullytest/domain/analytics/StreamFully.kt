package pini.mattia.shopfullytest.domain.analytics

import android.util.Log

class StreamFully(private val appPackageName: String, private val appVersionName:
String) {
    fun process(streamFullyEvent: StreamFullyEvent) {
        Log.d(TAG, "app: $appPackageName")
        Log.d(TAG, "version: $appVersionName")
        Log.d(TAG, "event: " + streamFullyEvent.eventType)
        logAttributes(streamFullyEvent)
    }
    private fun logAttributes(streamFullyEvent: StreamFullyEvent) {
        for ((key, value) in streamFullyEvent.attributes) {
            Log.d(TAG, "attribute[$key] : $value")
        }
    }
    private companion object {
        private const val TAG = "StreamFully"
    }
}