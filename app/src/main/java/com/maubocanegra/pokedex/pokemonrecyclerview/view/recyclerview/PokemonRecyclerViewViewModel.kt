package com.maubocanegra.pokedex.pokemonrecyclerview.view.recyclerview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maubocanegra.pokedex.pokemon.domain.usecase.GetPokemonDetailUseCase
import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonUiEntity
import com.maubocanegra.pokedex.pokemonlist.domain.model.UIState
import com.maubocanegra.pokedex.pokemonrecyclerview.domain.uistate.PokemonRecyclerViewUiState
import com.maubocanegra.pokedex.pokemonrecyclerview.domain.usecase.DeterminePokemonItemFetchingUseCase
import com.maubocanegra.pokedex.pokemonrecyclerview.domain.usecase.GetPokemonRecyclerViewUiStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonRecyclerViewViewModel @Inject constructor(
    private val getPokemonRecyclerViewUiStateUseCase: GetPokemonRecyclerViewUiStateUseCase,
    private val pokemonDetailFetchBeaconUseCase: DeterminePokemonItemFetchingUseCase,
    private val pokemonDetailUseCase: GetPokemonDetailUseCase
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

    // ---------------- Pokemon Detail ----------------

    private fun listenForPokemonItemDetailRequest(){
        viewModelScope.launch {
            pokemonDetailFetchBeaconUseCase.pokemonAttachmentState.collect{ state ->
                pokemonDetailUseCase(state.pokemonIdOrName)
                    .collect { pokemonDetail ->
                        if(pokemonDetail.pokemon!=null){
                            updatePokemonInList(pokemonDetail.pokemon)
                        }
                    }
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

    fun pokemonItemAttachesToScreen(pokemonIdOrName: String) {
        pokemonDetailFetchBeaconUseCase.handleAttachmentState(
            DeterminePokemonItemFetchingUseCase.AttachedToWindowState(pokemonIdOrName)
        )
    }

    fun pokemonItemDetachesFromScreen(pokemonIdOrName: String) {
        pokemonDetailFetchBeaconUseCase.handleAttachmentState(
            DeterminePokemonItemFetchingUseCase.DetachedFromWindowState(pokemonIdOrName)
        )
    }
}
