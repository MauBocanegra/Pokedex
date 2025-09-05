package com.maubocanegra.pokedex.pokemonlist.data.network.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.maubocanegra.pokedex.pokemonlist.data.network.PokemonApiService
import com.maubocanegra.pokedex.pokemonlist.domain.mapper.PokemonListMapper
import com.maubocanegra.pokedex.core.domain.model.PokemonListItemModel

class PokemonPagingSource(
    private val pokemonApiService: PokemonApiService,
) : PagingSource<Int, PokemonListItemModel>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PokemonListItemModel> {
        val offset = params.key ?: 0
        val limit = params.loadSize

        return try {
            val response = pokemonApiService.getPokemonList(limit, offset)
            val data = PokemonListMapper.mapToPokemonListModel(response).results

            LoadResult.Page(
                data = data,
                prevKey = if (offset == 0) null else offset - limit,
                nextKey = if (data.isEmpty()) null else offset + limit
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PokemonListItemModel>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(state.config.pageSize)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(state.config.pageSize)
        }
    }
}
