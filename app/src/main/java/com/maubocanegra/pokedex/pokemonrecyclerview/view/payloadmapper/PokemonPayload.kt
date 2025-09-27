package com.maubocanegra.pokedex.pokemonrecyclerview.view.payloadmapper

import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonSpriteUiEntity
import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonTypesUiEntity

sealed interface PokemonPayload {
    data class Name(val name: String?): PokemonPayload
    data class Url(val url: String?): PokemonPayload
    data class Types(val types: List<PokemonTypesUiEntity>): PokemonPayload
    data class Sprites(val sprites: PokemonSpriteUiEntity?): PokemonPayload

    data object ImageStateChanged: PokemonPayload
    data object DetailStateChanged: PokemonPayload
}