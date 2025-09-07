package com.maubocanegra.pokedex.pokemon.data.persistence.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon_list")
data class PokemonListItemDBEntity(
    @PrimaryKey val name: String,
    val url: String
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
    val sprites: String?, // stored as JSON
    val stats: String?, // stored as JSON
    val types: String?, // stored as JSON
    val officialArtwork: String?,
    val criesLatest: String?,
)