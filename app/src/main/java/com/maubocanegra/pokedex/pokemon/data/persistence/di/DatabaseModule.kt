package com.maubocanegra.pokedex.pokemon.data.persistence.di

import android.content.Context
import androidx.room.Room
import com.maubocanegra.pokedex.pokemon.data.persistence.PokemonDatabase
import com.maubocanegra.pokedex.pokemon.data.persistence.dao.PokemonDetailDao
import com.maubocanegra.pokedex.pokemon.data.persistence.dao.PokemonListDao
import com.maubocanegra.pokedex.pokemon.data.persistence.dao.PokemonPagerListDao
import com.maubocanegra.pokedex.pokemonstaggered.data.persistence.dao.PokemonRemoteKeysDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PokemonDatabase =
        Room.databaseBuilder(
            context,
            PokemonDatabase::class.java,
            "pokemon_db"
        )
            .fallbackToDestructiveMigration(true)
            .build()

    @Provides
    fun providePokemonListDao(db: PokemonDatabase): PokemonListDao =
        db.pokemonListDao()

    @Provides
    fun providePokemonDetailDao(db: PokemonDatabase): PokemonDetailDao =
        db.pokemonDetailDao()

    @Provides
    fun providePokemonPagerListDao(db: PokemonDatabase): PokemonPagerListDao =
        db.pokemonPagerListDao()

    @Provides
    fun providePokemonRemoteKeysDao(db: PokemonDatabase): PokemonRemoteKeysDao =
        db.pokemonRemoteKeysDao()
}