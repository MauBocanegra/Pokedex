package com.maubocanegra.pokedex.pokemon.data.persistence.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon_list")
data class PokemonListItemDBEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val url: String,
    val types: String? = null,       // JSON string of types
    val frontSpriteUrl: String? = null // URL for front sprite
)

@Entity(tableName = "pokemon_detail")
data class PokemonDetailDBEntity(
    @PrimaryKey val id: Int,
    val name: String?,
    val url: String?,
    val baseExperience: Int?,
    val height: Int?,
    val order: Int?,
    val weight: Int?,
    val abilities: String?, // stored as JSON
    val forms: String?, // stored as JSON
    val locationAreaEncounters: String?,
    val moves: String?, // stored as JSON
    @Embedded(prefix = "sprites_")
    val sprites: PokemonSpritesEmbeddable?,
    val stats: String?, // stored as JSON
    val types: String?, // stored as JSON
    val officialArtwork: String?,
    val criesLatest: String?,
)

data class PokemonSpritesEmbeddable(
    val backDefault: String? = null,
    val backShiny: String? = null,
    val frontDefault: String? = null,
    val backFemale: String? = null,
    val backShinyFemale: String? = null,
    val frontFemale: String? = null,
    val frontShiny: String? = null,

    @Embedded(prefix = "other_home_")
    val home: PokemonHomeSpritesEmbeddable? = null,

    @Embedded(prefix = "other_dream_")
    val dreamWorld: PokemonDreamWorldSpritesEmbeddable? = null
)

data class PokemonHomeSpritesEmbeddable(
    val homeFrontDefault: String? = null,
    val homeFrontFemale: String? = null
)

data class PokemonDreamWorldSpritesEmbeddable(
    val dreamWorldFrontDefault: String? = null,
    val dreamWorldFrontFemale: String? = null
)