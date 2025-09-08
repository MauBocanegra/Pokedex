package com.maubocanegra.pokedex.core.domain.model

data class PokemonListModel (
    val results: List<PokemonListItemModel>
)

data class PokemonListItemModel(
    val id: Int,
    val name: String,
    val url: String,
    val types: String? = null,  // JSON string
    val frontSpriteUrl: String? = null  // url
)