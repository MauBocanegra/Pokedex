package com.maubocanegra.pokedex.pokemon.data.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.maubocanegra.pokedex.pokemon.data.persistence.converter.PokemonTypeConverters
import com.maubocanegra.pokedex.pokemon.data.persistence.dao.PokemonDetailDao
import com.maubocanegra.pokedex.pokemon.data.persistence.dao.PokemonListDao
import com.maubocanegra.pokedex.pokemon.data.persistence.entity.PokemonDetailDBEntity
import com.maubocanegra.pokedex.pokemon.data.persistence.entity.PokemonListItemDBEntity

@Database(
    entities = [PokemonListItemDBEntity::class, PokemonDetailDBEntity::class],
    version = 1,
    exportSchema = true
)

@TypeConverters(PokemonTypeConverters::class)
abstract class PokemonDatabase: RoomDatabase() {
    abstract fun pokemonListDao(): PokemonListDao
    abstract fun pokemonDetailDao(): PokemonDetailDao
}