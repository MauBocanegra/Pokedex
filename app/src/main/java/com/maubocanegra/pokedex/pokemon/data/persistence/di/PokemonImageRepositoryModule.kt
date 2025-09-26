package com.maubocanegra.pokedex.pokemon.data.persistence.di

import com.maubocanegra.pokedex.pokemon.data.repository.PokemonImageRepositoryImpl
import com.maubocanegra.pokedex.pokemon.domain.repository.PokemonImageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module @InstallIn(SingletonComponent::class)
abstract class PokemonImageRepositoryModule {
    @Binds
    abstract fun providePokemonImageRepository(
        pokemonImageRepositoryImpl: PokemonImageRepositoryImpl
    ): PokemonImageRepository
}