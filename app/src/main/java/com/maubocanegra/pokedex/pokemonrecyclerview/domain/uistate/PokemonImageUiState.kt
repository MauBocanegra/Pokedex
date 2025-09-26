package com.maubocanegra.pokedex.pokemonrecyclerview.domain.uistate

import android.graphics.Bitmap

sealed interface PokemonImageUiState {
    data object Idle : PokemonImageUiState
    data object Loading : PokemonImageUiState
    data class Ready(val bitmap: Bitmap) : PokemonImageUiState
    data object Error : PokemonImageUiState
}