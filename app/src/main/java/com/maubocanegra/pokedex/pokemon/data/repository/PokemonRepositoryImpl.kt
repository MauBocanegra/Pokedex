package com.maubocanegra.pokedex.pokemon.data.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.maubocanegra.pokedex.core.network.util.APIResult
import com.maubocanegra.pokedex.pokemondetail.data.mapper.toUiEntity
import com.maubocanegra.pokedex.pokemondetail.domain.model.PokemonDetailResult
import com.maubocanegra.pokedex.pokemonlist.data.network.PokemonApiService
import com.maubocanegra.pokedex.pokemonlist.data.network.datasource.PokemonPagingSource
import com.maubocanegra.pokedex.core.domain.model.PokemonListItemModel
import com.maubocanegra.pokedex.pokemon.data.persistence.PokemonDatabase
import com.maubocanegra.pokedex.pokemon.data.persistence.dao.PokemonDetailDao
import com.maubocanegra.pokedex.pokemon.data.persistence.dao.PokemonPagerListDao
import com.maubocanegra.pokedex.pokemon.data.persistence.mapper.toDBEntity
import com.maubocanegra.pokedex.pokemon.data.persistence.mapper.toListDBEntity
import com.maubocanegra.pokedex.pokemon.data.persistence.mapper.toModel
import com.maubocanegra.pokedex.pokemon.data.persistence.mapper.toUiEntity
import com.maubocanegra.pokedex.pokemon.domain.repository.PokemonRepository
import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonUiEntity
import com.maubocanegra.pokedex.pokemonstaggered.data.paging.PokemonRemoteMediator
import com.maubocanegra.pokedex.pokemonstaggered.data.persistence.dao.PokemonRemoteKeysDao
import com.maubocanegra.pokedex.pokemonstaggered.data.persistence.mapper.toUiEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PokemonRepositoryImpl @Inject constructor(
    private val pokemonApiService: PokemonApiService,
    private val pokemonPagerListDao: PokemonPagerListDao,
    private val pokemonDetailDao: PokemonDetailDao,
    private val pokemonRemoteKeysDao: PokemonRemoteKeysDao,
    private val pokemonDatabase: PokemonDatabase
): PokemonRepository {

    override fun getPokemonPager(pageSize: Int): Flow<PagingData<PokemonListItemModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PokemonPagingSource(pokemonApiService) }
        ).flow
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun pagingFlow(config: PagingConfig): Flow<PagingData<PokemonUiEntity>> {
        return Pager(
            config = config,
            remoteMediator = PokemonRemoteMediator(
                service = pokemonApiService,
                database = pokemonDatabase,
                listDao = pokemonPagerListDao,
                keysDao = pokemonRemoteKeysDao
            ),
            pagingSourceFactory = { pokemonPagerListDao.pagingSource() }
        ).flow.map { pagingData ->
            pagingData.map { pokemonListDBEntity ->
                Log.d("MauDebug","pagingFlowData: ${pokemonListDBEntity}")
                pokemonListDBEntity.toUiEntity()
            }
        }
    }

    override suspend fun getPokemonDetail(pokemonId: Int): PokemonDetailResult {
        return try {

            // --- 1. Try DB cache ---
            val cached = pokemonDetailDao.getPokemonDetailById(pokemonId).firstOrNull()

            if (cached != null) {
                return PokemonDetailResult.Success(cached.toUiEntity())
            }

            // 2. Fetch from API
            val response = pokemonApiService.getPokemonDetail(pokemonId)
            when (response) {
                is APIResult.Success -> {
                    val uiEntity = PokemonDetailResult.Success(response.data.toUiEntity())

                    // 3. Save to DB
                    pokemonDetailDao.insertPokemonDetail(uiEntity.pokemon.toDBEntity())
                    //pokemonPagerListDao.insertPokemonList(
                        //listOf(uiEntity.pokemon.toDBEntity().toListDBEntity())
                    //)
                    uiEntity
                }
                is APIResult.Failure -> PokemonDetailResult.Error(response.throwable, response.message)
                APIResult.Loading -> throw IllegalStateException("Loading should not propagate here")
            }
        } catch (e: Exception) {
            PokemonDetailResult.Error(e, e.localizedMessage)
        }
    }

    override suspend fun getPokemonList(
        limit: Int,
        offset: Int
    ): Flow<APIResult<List<PokemonListItemModel>>> = flow {
        emit(APIResult.Loading)

        // 1. Emit from DB cache for this page only
        val cached = pokemonPagerListDao.getPokemonPage(limit, offset).firstOrNull()
        if (!cached.isNullOrEmpty()) {
            val toBeEmited = APIResult.Success(cached.map { it.toModel() })
            emit(toBeEmited)
            return@flow
        }

        // 2. Fetch from API
        val response = try {
            pokemonApiService.getPokemonList(limit, offset)
        } catch (e: Exception) {
            emit(APIResult.Failure(e))
            return@flow
        }

        // 3. Parse results
        val items = response.results?.mapNotNull { result ->
            if (!result.name.isNullOrEmpty() && !result.url.isNullOrEmpty()) {
                PokemonListItemModel(
                    result.url.trimEnd('/').substringAfterLast('/').toInt(),
                    result.name,
                    result.url
                )
            } else null
        }.orEmpty()

        if (items.isNotEmpty()) {
            // Insert with extracted IDs to enforce deterministic ordering
            pokemonPagerListDao.insertPokemonList(items.map { it.toDBEntity() })

            // Fetch updated page from DB to guarantee order consistency
            val updatedCache = pokemonPagerListDao.getPokemonPage(limit, offset).firstOrNull()
            if (!updatedCache.isNullOrEmpty()) {
                emit(APIResult.Success(updatedCache.map { it.toModel() }))
                return@flow
            }
        }

        // Fallback: emit API results if DB somehow empty
        emit(APIResult.Success(items))
    }
}
