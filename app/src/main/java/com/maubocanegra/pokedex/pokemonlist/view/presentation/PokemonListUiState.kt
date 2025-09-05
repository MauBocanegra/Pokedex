package com.maubocanegra.pokedex.pokemonlist.view.presentation

import com.maubocanegra.pokedex.core.domain.model.PokemonListModel
import com.maubocanegra.pokedex.pokemonlist.domain.model.UIState

data class PokemonListUiState(
    val uiState: UIState = UIState.LOADING,
    val data: PokemonListModel? = null,
    val errorMessage: String? = null,
)
