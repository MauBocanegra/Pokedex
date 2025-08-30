package com.maubocanegra.pokedex.pokemondetail.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maubocanegra.pokedex.pokemondetail.domain.uistate.PokemonDetailUiState
import com.maubocanegra.pokedex.pokemondetail.domain.usecase.GetPokemonDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val getPokemonDetailUseCase: GetPokemonDetailUseCase
): ViewModel(){
    private val _pokemonDetailState = MutableStateFlow(PokemonDetailUiState())
    val pokemonDetailState: StateFlow<PokemonDetailUiState> = _pokemonDetailState

    fun getPokemonDetail(pokemonIdOrName: String) {
        getPokemonDetailUseCase(pokemonIdOrName)
            .onEach { state ->
                _pokemonDetailState.value = state
            }
            .launchIn(viewModelScope)
    }
}