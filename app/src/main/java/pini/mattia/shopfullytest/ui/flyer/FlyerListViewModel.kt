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
import pini.mattia.shopfullytest.domain.error.FlyerError
import pini.mattia.shopfullytest.domain.flyer.Flyer
import pini.mattia.shopfullytest.domain.flyer.UseCaseGetFlyers
import javax.inject.Inject

@HiltViewModel
class FlyerListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val useCaseGetFlyers: UseCaseGetFlyers
) : ViewModel() {

    private val _showOnlySeen = MutableStateFlow(false)

    private val _viewState = MutableStateFlow<FlyerListState>(FlyerListState.Loading)
    val viewState: StateFlow<FlyerListState> = _viewState

    private val _allFlyers: MutableStateFlow<List<Flyer>> = MutableStateFlow(emptyList())

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

}

sealed interface FlyerListState {
    data object Loading : FlyerListState
    data class Content(val flyers: List<Flyer>, val isFilterEnabled: Boolean) : FlyerListState
    data class Error(val error: FlyerError) : FlyerListState
}