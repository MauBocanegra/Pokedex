package com.maubocanegra.pokedex.pokemonrecyclerview.domain.uistate

import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonUiEntity

sealed interface PokemonDetailItemState {
    data object Idle: PokemonDetailItemState
    data object Loading: PokemonDetailItemState
    data class Ready(val pokemonDetail: PokemonUiEntity): PokemonDetailItemState
    data object Error: PokemonDetailItemState
}