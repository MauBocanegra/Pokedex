package com.maubocanegra.pokedex.pokemonlist.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class PokemonListResponse(
    val count: Int? = null,
    val next: String? = null,
    val previous: String? = null,
    val results: List<PokemonURLResponse>? = null
)

@Serializable
data class PokemonURLResponse(
    val name: String? = null,
    val url: String? = null
)
