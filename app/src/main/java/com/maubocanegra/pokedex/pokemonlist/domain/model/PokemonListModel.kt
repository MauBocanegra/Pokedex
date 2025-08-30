package com.maubocanegra.pokedex.pokemonlist.domain.model

data class PokemonListModel (
    val results: List<PokemonListItemModel>
)

data class PokemonListItemModel(
    val name: String,
    val url: String,
)