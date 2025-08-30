package com.maubocanegra.pokedex.pokemonlist.view.mapper

import com.maubocanegra.pokedex.pokemonlist.domain.model.PokemonListModel
import com.maubocanegra.pokedex.pokemonlist.domain.model.UIState
import com.maubocanegra.pokedex.pokemonlist.view.presentation.PokemonListUiState

// TODO this should be removed
object PokemonListUiMapper {

    private fun mapToUiState(
        uiState: UIState,
        data: PokemonListModel? = null,
        errorMessage: String? = null
    ): PokemonListUiState {
        return PokemonListUiState(
            uiState = uiState,
            data = data,
            errorMessage = errorMessage
        )
    }

    fun success(data: PokemonListModel) =
        mapToUiState(UIState.SUCCESS, data)

    fun error(message: String?) =
        mapToUiState(UIState.FAILED, errorMessage = message)

    fun loading() =
        mapToUiState(UIState.LOADING)
}
