package com.maubocanegra.pokedex.pokemonlist.domain.repository

import androidx.paging.PagingData
import com.maubocanegra.pokedex.pokemondetail.domain.model.PokemonDetailResult
import com.maubocanegra.pokedex.core.domain.model.PokemonListItemModel
import com.maubocanegra.pokedex.core.network.util.APIResult
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {
    fun getPokemonPager(
        pageSize: Int
    ): Flow<PagingData<PokemonListItemModel>>

    suspend fun getPokemonList(
        limit: Int = 20,
        offset: Int = 0,
    ): Flow<APIResult<List<PokemonListItemModel>>>

    suspend fun getPokemonDetail(
        pokemonIdOrName: String
    ): PokemonDetailResult
}
