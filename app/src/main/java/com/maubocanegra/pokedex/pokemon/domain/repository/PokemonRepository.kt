package com.maubocanegra.pokedex.pokemon.domain.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.maubocanegra.pokedex.core.domain.model.PokemonListItemModel
import com.maubocanegra.pokedex.core.network.util.APIResult
import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonUiEntity
import com.maubocanegra.pokedex.pokemondetail.domain.model.PokemonDetailResult
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {
    fun getPokemonPager(
        pageSize: Int
    ): Flow<PagingData<PokemonListItemModel>>

    fun pagingFlow(config: PagingConfig): Flow<PagingData<PokemonUiEntity>>

    suspend fun getPokemonList(
        limit: Int = 20,
        offset: Int = 0,
    ): Flow<APIResult<List<PokemonListItemModel>>>

    suspend fun getPokemonDetail(
        pokemonId: Int
    ): PokemonDetailResult
}