package com.maubocanegra.pokedex.core.navigation

import kotlinx.serialization.Serializable


@Serializable
object PokemonListScreenNavigation

@Serializable
data class PokemonDetailScreenNavigation(
    val pokemonName: String,
    val pokemonUrl: String,
)