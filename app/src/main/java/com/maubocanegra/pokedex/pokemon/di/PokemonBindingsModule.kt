package com.maubocanegra.pokedex.pokemon.di

import com.maubocanegra.pokedex.pokemon.data.repository.PokemonRepositoryImpl
import com.maubocanegra.pokedex.pokemon.domain.repository.PokemonRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class PokemonBindingsModule {
    @Binds
    abstract fun bindPokemonRepository(
        pokemonRepositoryImpl: PokemonRepositoryImpl
    ): PokemonRepository
}