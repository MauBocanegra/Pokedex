package com.maubocanegra.pokedex.pokemonrecyclerview.domain.uistate

import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonUiEntity
import com.maubocanegra.pokedex.pokemonlist.domain.model.UIState

data class PokemonRecyclerViewUiState(
    val pokemonList: List<PokemonUiEntity> = emptyList(),
    val uiState: UIState = UIState.LOADING,
    val errorMessage: String? = null,
)
