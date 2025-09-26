package com.maubocanegra.pokedex.pokemonrecyclerview.view.model

import com.maubocanegra.pokedex.pokemonrecyclerview.domain.uistate.PokemonImageUiState

data class PokemonItemImageUiModel(
    val id: Int,
    val name: String,
    val frontDefaultUrl: String,
    val imageState: PokemonImageUiState = PokemonImageUiState.Idle
)
