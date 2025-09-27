package com.maubocanegra.pokedex.pokemonrecyclerview.view.recyclerview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maubocanegra.pokedex.pokemon.domain.usecase.GetPokemonDetailUseCase
import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonUiEntity
import com.maubocanegra.pokedex.pokemonlist.domain.model.UIState
import com.maubocanegra.pokedex.pokemonrecyclerview.domain.uistate.PokemonDetailItemState
import com.maubocanegra.pokedex.pokemonrecyclerview.domain.uistate.PokemonRecyclerViewUiState
import com.maubocanegra.pokedex.pokemonrecyclerview.domain.usecase.DeterminePokemonItemFetchingUseCase
import com.maubocanegra.pokedex.pokemonrecyclerview.domain.usecase.GetPokemonRecyclerViewUiStateUseCase
import com.maubocanegra.pokedex.pokemonrecyclerview.domain.usecase.LoadPokemonItemImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonRecyclerViewViewModel @Inject constructor(
    private val getPokemonRecyclerViewUiStateUseCase: GetPokemonRecyclerViewUiStateUseCase,
    private val pokemonDetailFetchBeaconUseCase: DeterminePokemonItemFetchingUseCase,
    private val pokemonDetailUseCase: GetPokemonDetailUseCase,
    private val loadPokemonItemImageUseCase: LoadPokemonItemImageUseCase,
): ViewModel(){

    private val _uiState = MutableStateFlow(
        PokemonRecyclerViewUiState(
            pokemonList = emptyList(),
            uiState = UIState.LOADING,
            errorMessage = null
        )
    )
    val uiState: StateFlow<PokemonRecyclerViewUiState> = _uiState

    init {
        loadInitialPokemonList()
        listenForPokemonItemDetailRequest()
        mirrorImageStates()
    }

    private fun loadInitialPokemonList() {
        loadPokemonList(isInitial = true)
    }

    fun loadNextPage() {
        val state = _uiState.value
        //avoid multiple fetching if already fetching next page
        if (state.isLoadingNextPage || state.endReached) return

        loadPokemonList(isInitial = false)
    }

    private fun loadPokemonList(isInitial: Boolean) {
        viewModelScope.launch {
            val state = _uiState.value

            _uiState.value = state.copy(
                uiState = if (isInitial) UIState.LOADING else UIState.LOADING_NEXT_PAGE,
                isLoadingNextPage = !isInitial
            )

            val limit = state.pageSize
            val offset = if (isInitial) 0 else state.currentOffset

            getPokemonRecyclerViewUiStateUseCase(limit, offset)
                .catch { throwable ->
                    _uiState.value = PokemonRecyclerViewUiState(
                        pokemonList = emptyList(),
                        uiState = UIState.FAILED,
                        errorMessage = throwable.message ?: "Unknown error"
                    )
                }
                .collect { newState ->
                    val newItems = newState.pokemonList

                    if(isInitial) {
                        _uiState.value = newState.copy(
                            pokemonList = newItems,
                            currentOffset = newItems.size,
                            isLoadingNextPage = false,
                            endReached = newItems.isEmpty()
                        )
                    } else {
                        val combined = state.pokemonList + newItems
                        _uiState.value = state.copy(
                            pokemonList = combined,
                            currentOffset = combined.size,
                            isLoadingNextPage = false,
                            endReached = newItems.isEmpty()
                        )
                    }
                }
        }
    }

    // ---------- Images: mirror use-case state and delegate events ----------

    private fun mirrorImageStates() {
        viewModelScope.launch {
            loadPokemonItemImageUseCase.imageStateById.collectLatest { map ->
                _uiState.value = _uiState.value.copy(imageStateById = map)
            }
        }
    }

    fun onItemRecycledForImage(pokemonId: Int) {
        loadPokemonItemImageUseCase.onItemRecycled(pokemonId)
    }

    override fun onCleared() {
        loadPokemonItemImageUseCase.clearAll()
        super.onCleared()
    }

    // ---------------- Pokemon Detail ----------------

    private fun listenForPokemonItemDetailRequest(){
        viewModelScope.launch {
            pokemonDetailFetchBeaconUseCase.pokemonAttachmentState.collect{ state ->

                _uiState.value = _uiState.value.copy(
                    detailStateById = _uiState.value.detailStateById
                        .toMutableMap()
                        .apply {
                            put(
                                state.pokemonId,
                                PokemonDetailItemState.Loading
                            )
                        }
                )

                val sharedFlow = pokemonDetailUseCase(state.pokemonId).shareIn(
                    scope = viewModelScope,
                    started = SharingStarted.Eagerly,
                    replay = 1
                )

                // Update each pokemon detail in list
                sharedFlow
                    .map { it.pokemon }
                    .filterNotNull()
                    .onEach { pokemon ->
                        updatePokemonInList(pokemon)
                        _uiState.value = _uiState.value.copy(
                            detailStateById = _uiState.value.detailStateById
                                .toMutableMap().apply {
                                    put(
                                        state.pokemonId,
                                        PokemonDetailItemState.Ready
                                    )
                                }
                        )
                    }
                    .catch {
                        _uiState.value = _uiState.value.copy(
                            detailStateById = _uiState.value.detailStateById
                                .toMutableMap().apply {
                                    put(
                                        state.pokemonId,
                                        PokemonDetailItemState.Error
                                    )
                                }
                        )
                    }
                    .launchIn(viewModelScope)

                sharedFlow
                    .map { detail ->
                        detail.pokemon?.sprites?.other?.homeSprites?.homeFrontDefault
                            ?.takeIf { it.isNotBlank() }
                            ?: detail.pokemon?.sprites?.frontDefault?.takeIf { it.isNotBlank() }
                    }
                    .filterNotNull()
                    .distinctUntilChanged()
                    .take(1)
                    .onEach { imageUrl ->
                        loadPokemonItemImageUseCase.onItemBound(
                            scope = viewModelScope,
                            itemId = state.pokemonId,
                            pngUrl = imageUrl
                        )
                    }
                    .launchIn(viewModelScope)
            }
        }
    }

    private fun updatePokemonInList(updatedPokemon: PokemonUiEntity) {
        val currentList = _uiState.value.pokemonList

        // Create a new list with the updated item
        val newList = currentList.map { existingPokemon ->
            if (existingPokemon.id == updatedPokemon.id) {
                updatedPokemon // replace with the updated Pokemon
            } else {
                existingPokemon // keep the old one
            }
        }

        _uiState.value = _uiState.value.copy(
            pokemonList = newList
        )
    }

    // ---------------- Item attachment ----------------

    fun pokemonItemAttachesToScreen(pokemonId: Int) {
        pokemonDetailFetchBeaconUseCase.handleAttachmentState(
            DeterminePokemonItemFetchingUseCase.AttachedToWindowState(pokemonId)
        )
    }

    fun pokemonItemDetachesFromScreen(pokemonId: Int) {
        pokemonDetailFetchBeaconUseCase.handleAttachmentState(
            DeterminePokemonItemFetchingUseCase.DetachedFromWindowState(pokemonId)
        )
    }
}
