package com.maubocanegra.pokedex.pokemonlist.domain.repository

import androidx.paging.PagingData
import com.maubocanegra.pokedex.pokemondetail.domain.model.PokemonDetailResult
import com.maubocanegra.pokedex.pokemonlist.domain.model.PokemonListItemModel
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {
    fun getPokemonPager(
        pageSize: Int
    ): Flow<PagingData<PokemonListItemModel>>

    suspend fun getPokemonDetail(
        pokemonIdOrName: String
    ): PokemonDetailResult
}
