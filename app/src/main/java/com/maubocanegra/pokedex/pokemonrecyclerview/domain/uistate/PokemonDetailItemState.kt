package com.maubocanegra.pokedex.pokemonrecyclerview.domain.uistate

sealed interface PokemonDetailItemState {
    data object Idle: PokemonDetailItemState
    data object Loading: PokemonDetailItemState
    data object Ready: PokemonDetailItemState
    data object Error: PokemonDetailItemState
}