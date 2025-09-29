package com.maubocanegra.pokedex.pokemonstaggered.data.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.maubocanegra.pokedex.pokemonstaggered.data.paging.PokemonRemoteKeys

@Dao
interface PokemonRemoteKeysDao {
    @Query("SELECT * FROM pokemon_remote_keys WHERE id = :id")
    suspend fun remoteKeysById(id: Int): PokemonRemoteKeys?

    @Query("SELECT MAX(nextOffset) FROM pokemon_remote_keys")
    suspend fun maxNextOffset(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(keys: List<PokemonRemoteKeys>)

    @Query("DELETE FROM pokemon_remote_keys")
    suspend fun clearRemoteKeys()
}