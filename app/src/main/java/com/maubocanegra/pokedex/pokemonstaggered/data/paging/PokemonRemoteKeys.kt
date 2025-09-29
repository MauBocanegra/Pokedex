package com.maubocanegra.pokedex.pokemonstaggered.data.paging

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon_remote_keys")
data class PokemonRemoteKeys(
    @PrimaryKey val id: Int,        // matches pokemon_list.id
    val prevOffset: Int?,           // offset for PREPEND (unused; stays null)
    val nextOffset: Int?            // offset for APPEND
)