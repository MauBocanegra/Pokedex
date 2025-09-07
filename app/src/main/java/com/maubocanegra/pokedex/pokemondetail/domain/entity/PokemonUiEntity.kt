package com.maubocanegra.pokedex.pokemondetail.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class PokemonUiEntity(
    val id: Int,
    val name: String?,
    val url: String?,
    val baseExperience: Int?,
    val height: Int?,
    val order: Int?,
    val weight: Int?,
    val abilities: List<PokemonAbilityUiEntity>?,
    val forms: List<CustomPokemonAPIResource>?,
    val locationAreaEncounters: String?,
    val moves: List<PokemonMoveUiEntity>?,
    val sprites: List<PokemonSpriteUiEntity>?,
    val stats: List<PokemonStatUiEntity>?,
    val types: List<PokemonTypesUiEntity>?,
    val officialArtwork: String?,
    val criesLatest: String?,
)

@Serializable
data class CustomPokemonAPIResource(
    val name: String,
    val url: String,
)

@Serializable
data class PokemonAbilityUiEntity(
    val isHidden: Boolean,
    val slot: Int,
    val ability: CustomPokemonAPIResource,
)

@Serializable
data class PokemonMoveUiEntity (
    val move: CustomPokemonAPIResource,
)

@Serializable
data class PokemonSpriteUiEntity(
    val backDefault: String? = null,
    val backShiny: String? = null,
    val frontDefault: String? = null,
)

@Serializable
data class PokemonStatUiEntity(
    val baseStat: Int,
    val effort: Int,
    val stat: CustomPokemonAPIResource,
)

@Serializable
data class PokemonTypesUiEntity(
    val slot: Int,
    val type: CustomPokemonAPIResource,
)
