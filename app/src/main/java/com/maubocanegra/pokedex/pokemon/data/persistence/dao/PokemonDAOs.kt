package com.maubocanegra.pokedex.pokemon.data.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.maubocanegra.pokedex.pokemon.data.persistence.entity.PokemonDetailDBEntity
import com.maubocanegra.pokedex.pokemon.data.persistence.entity.PokemonListItemDBEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonListDao {
    @Query("SELECT * FROM pokemon_list")
    fun getAllPokemon(): Flow<List<PokemonListItemDBEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemonList(items: List<PokemonListItemDBEntity>)

    @Query("DELETE FROM pokemon_list")
    suspend fun clearPokemonList()
}

@Dao
interface PokemonDetailDao {
    @Query("SELECT * FROM pokemon_detail WHERE id = :id")
    fun getPokemonDetailById(id: Int): Flow<PokemonDetailDBEntity?>

    @Query("SELECT * FROM pokemon_detail WHERE name = :name")
    fun getPokemonDetailByName(name: String): Flow<PokemonDetailDBEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemonDetail(detail: PokemonDetailDBEntity)

    @Query("DELETE FROM pokemon_detail WHERE id = :id")
    suspend fun deletePokemonDetail(id: Int)
}
