package com.maubocanegra.pokedex.pokemon.data.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.maubocanegra.pokedex.pokemon.data.persistence.converter.PokemonTypeConverters
import com.maubocanegra.pokedex.pokemon.data.persistence.dao.PokemonDetailDao
import com.maubocanegra.pokedex.pokemon.data.persistence.dao.PokemonListDao
import com.maubocanegra.pokedex.pokemon.data.persistence.dao.PokemonPagerListDao
import com.maubocanegra.pokedex.pokemon.data.persistence.entity.PokemonDetailDBEntity
import com.maubocanegra.pokedex.pokemon.data.persistence.entity.PokemonListItemDBEntity
import com.maubocanegra.pokedex.pokemonstaggered.data.paging.PokemonRemoteKeys
import com.maubocanegra.pokedex.pokemonstaggered.data.persistence.dao.PokemonRemoteKeysDao

@Database(
    entities = [
        PokemonListItemDBEntity::class,
        PokemonDetailDBEntity::class,
        PokemonRemoteKeys::class
    ],
    version = 7,
    exportSchema = true
)

@TypeConverters(PokemonTypeConverters::class)
abstract class PokemonDatabase: RoomDatabase() {
    abstract fun pokemonListDao(): PokemonListDao
    abstract fun pokemonDetailDao(): PokemonDetailDao
    abstract fun pokemonPagerListDao(): PokemonPagerListDao
    abstract fun pokemonRemoteKeysDao(): PokemonRemoteKeysDao
}