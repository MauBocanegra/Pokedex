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
        loadPokemonList()
        listenForPokemonItemDetailRequest()
    }

    private fun loadPokemonList(limit: Int = 20, offset: Int = 0) {
        viewModelScope.launch {
            getPokemonRecyclerViewUiStateUseCase(limit, offset)
                .catch { throwable ->
                    _uiState.value = PokemonRecyclerViewUiState(
                        pokemonList = emptyList(),
                        uiState = UIState.FAILED,
                        errorMessage = throwable.message ?: "Unknown error"
                    )
                }
                .collect { state ->
                    _uiState.value = state
                }
        }
    }

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

        // Update the StateFlow with the new list while keeping other UIState fields intact
        _uiState.value = _uiState.value.copy(
            pokemonList = newList
        )
    }

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
