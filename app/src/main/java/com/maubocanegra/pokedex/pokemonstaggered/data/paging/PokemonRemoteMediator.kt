package com.maubocanegra.pokedex.pokemonstaggered.data.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.maubocanegra.pokedex.pokemon.data.persistence.PokemonDatabase
import com.maubocanegra.pokedex.pokemon.data.persistence.dao.PokemonPagerListDao
import com.maubocanegra.pokedex.pokemon.data.persistence.entity.PokemonListItemDBEntity
import com.maubocanegra.pokedex.pokemonlist.data.network.PokemonApiService
import com.maubocanegra.pokedex.pokemonstaggered.data.persistence.dao.PokemonRemoteKeysDao
import com.maubocanegra.pokedex.pokemonstaggered.data.persistence.mapper.toDBEntities

@OptIn(ExperimentalPagingApi::class)
class PokemonRemoteMediator(
    private val service: PokemonApiService,
    private val database: PokemonDatabase,
    private val listDao: PokemonPagerListDao,
    private val keysDao: PokemonRemoteKeysDao
) : RemoteMediator<Int, PokemonListItemDBEntity>() {

    override suspend fun initialize(): InitializeAction = InitializeAction.SKIP_INITIAL_REFRESH

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PokemonListItemDBEntity>
    ): MediatorResult {
        return try {
            val pageSize = state.config.pageSize
            val offset = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    keysDao.maxNextOffset() ?: listDao.countRows()

                    /*
                    val last = state.lastItemOrNull()
                    val key = last?.let { keysDao.remoteKeysById(it.id)?.nextOffset }
                    key ?: listDao.countRows()
                    */
                }
            }

            val listResponse = service.getPokemonList(limit = pageSize, offset = offset)
            val items: List<PokemonListItemDBEntity> = listResponse.toDBEntities()
            val end = when {
                listResponse.next != null -> listResponse.next.isBlank()
                listResponse.count != null -> (offset + items.size) >= (listResponse.count ?: 0)
                else -> items.size < pageSize
            }

            Log.d(
                "Mediator",
                "load=$loadType off=$offset size=${items.size} next=${listResponse.next} count=${listResponse.count} end=$end"
            )

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    keysDao.clearRemoteKeys()
                    listDao.clearPokemonList()
                }
                val next = if (end) null else offset + items.size
                val keys = items.map { PokemonRemoteKeys(id = it.id, prevOffset = null, nextOffset = next) }
                keysDao.insertAll(keys)

                listDao.insertPokemonList(items)
            }

            Log.d("MauDebug", "type=$loadType off=$offset size=${items.size} next=${listResponse.next} count=${listResponse.count} end=$end")

            MediatorResult.Success(endOfPaginationReached = end)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }


}