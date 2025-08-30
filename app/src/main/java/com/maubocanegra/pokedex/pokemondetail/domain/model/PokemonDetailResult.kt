package com.maubocanegra.pokedex.pokemondetail.domain.model

import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonUiEntity

sealed class PokemonDetailResult {
    data class Success(val pokemon: PokemonUiEntity) : PokemonDetailResult()
    data class Error(val throwable: Throwable, val message: String? = null) : PokemonDetailResult()
}