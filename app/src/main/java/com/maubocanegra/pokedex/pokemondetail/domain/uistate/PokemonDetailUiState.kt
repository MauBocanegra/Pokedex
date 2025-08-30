package com.maubocanegra.pokedex.pokemondetail.domain.uistate

import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonUiEntity
import com.maubocanegra.pokedex.pokemonlist.domain.model.UIState

data class PokemonDetailUiState(
    val pokemon: PokemonUiEntity? = null,
    val uiState: UIState = UIState.LOADING,
    val errorMessage: String? = null
)
