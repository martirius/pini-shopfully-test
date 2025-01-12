package pini.mattia.shopfullytest.ui.flyer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import pini.mattia.shopfullytest.domain.analytics.StreamFully
import pini.mattia.shopfullytest.domain.error.FlyerError
import pini.mattia.shopfullytest.domain.flyer.Flyer
import pini.mattia.shopfullytest.domain.flyer.UseCaseGetFlyers
import java.util.Date
import javax.inject.Inject

private const val IMPRESSION_DURATION_THRESHOLD = 1000

@HiltViewModel
class FlyerListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val useCaseGetFlyers: UseCaseGetFlyers,
    private val streamFully: StreamFully
) : ViewModel() {

    private val _showOnlySeen = MutableStateFlow(false)

    private val _viewState = MutableStateFlow<FlyerListState>(FlyerListState.Loading)
    val viewState: StateFlow<FlyerListState> = _viewState

    private val _allFlyers: MutableStateFlow<List<Flyer>> = MutableStateFlow(emptyList())

    private var flyerOpenedTime: Long = 0L

    init {
        viewModelScope.launch {
            _allFlyers.combine(
                _showOnlySeen,
            ) { f1, f2 ->
                if (f1.isNotEmpty()) { //put this if to not show the empty page at app startup
                    val filteredFlyers = if (f2) f1.filter { it.isAlreadySeen } else f1
                    _viewState.emit(FlyerListState.Content(filteredFlyers, f2))
                }
            }.collect()
        }
        loadFlyers()
    }

    fun loadFlyers() {
        viewModelScope.launch {
            _viewState.emit(FlyerListState.Loading)
            useCaseGetFlyers.execute().fold(
                ifLeft = { error ->
                    _viewState.emit(FlyerListState.Error(error))
                },
                ifRight = { flyers ->
                    _allFlyers.emit(flyers)
                }
            )
        }
    }

    fun flyerSelected(flyer: Flyer) {
        val currentState = viewState.value
        if (currentState is FlyerListState.Content) {
            flyerOpenedTime = Date().time
            _viewState.value = currentState.copy(selectedFlyer = flyer)
            val streamFullyEvent = StreamFullyEvents.FlyerOpen(
                flyer.retailerId,
                flyer.id,
                flyer.title,
                _allFlyers.value.indexOfFirst { it.id == flyer.id },
                !flyer.isAlreadySeen
            )
            streamFully.process(streamFullyEvent)
        }
    }

    fun flyerDetailDismissed() {
        val currentState = viewState.value
        if (currentState is FlyerListState.Content && currentState.selectedFlyer != null) {
            val sessionDuration = (Date().time - flyerOpenedTime).toInt()
            val flyerSessionEvent = StreamFullyEvents.FlyerSession(
                currentState.selectedFlyer.id,
                sessionDuration,
                !currentState.selectedFlyer.isAlreadySeen
            )

            val readFlyer = currentState.selectedFlyer.copy(isAlreadySeen = true)
            val updatedFlyers = replaceFlyer(_allFlyers.value, readFlyer)
            _viewState.value = currentState.copy(selectedFlyer = null)
            _allFlyers.value = updatedFlyers

            streamFully.process(flyerSessionEvent)

        }
    }

    fun filterSwitched(status: Boolean) {
        _showOnlySeen.value = status
    }

    fun onImpression(duration: Int, percentage: Float, flyerId: Int) {
        if(duration > IMPRESSION_DURATION_THRESHOLD) {
            val impressionEvent =
                StreamFullyEvents.Viewability(flyerId, duration, (percentage * 100).toInt())
            streamFully.process(impressionEvent)
        }
    }

    private fun replaceFlyer(list: List<Flyer>, newItem: Flyer): List<Flyer> {
        val mutableList = list.toMutableList()
        val index = list.indexOfFirst { it.id == newItem.id }
        if (index != -1) {
            mutableList[index] = newItem
        }
        return mutableList
    }
}

sealed interface FlyerListState {
    data object Loading : FlyerListState
    data class Content(
        val flyers: List<Flyer>,
        val isFilterEnabled: Boolean,
        val selectedFlyer: Flyer? = null
    ) : FlyerListState

    data class Error(val error: FlyerError) : FlyerListState
}