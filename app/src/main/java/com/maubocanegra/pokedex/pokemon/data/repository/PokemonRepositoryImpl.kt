package com.maubocanegra.pokedex.pokemon.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.maubocanegra.pokedex.core.network.util.APIResult
import com.maubocanegra.pokedex.pokemondetail.data.mapper.toUiEntity
import com.maubocanegra.pokedex.pokemondetail.domain.model.PokemonDetailResult
import com.maubocanegra.pokedex.pokemonlist.data.network.PokemonApiService
import com.maubocanegra.pokedex.pokemonlist.data.network.datasource.PokemonPagingSource
import com.maubocanegra.pokedex.core.domain.model.PokemonListItemModel
import com.maubocanegra.pokedex.pokemon.data.persistence.dao.PokemonDetailDao
import com.maubocanegra.pokedex.pokemon.data.persistence.dao.PokemonListDao
import com.maubocanegra.pokedex.pokemon.data.persistence.mapper.toDBEntity
import com.maubocanegra.pokedex.pokemon.data.persistence.mapper.toModel
import com.maubocanegra.pokedex.pokemon.data.persistence.mapper.toUiEntity
import com.maubocanegra.pokedex.pokemonlist.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class PokemonRepositoryImpl @Inject constructor(
    private val pokemonApiService: PokemonApiService,
    private val pokemonListDao: PokemonListDao,
    private val pokemonDetailDao: PokemonDetailDao
): PokemonRepository{

    override fun getPokemonPager(pageSize: Int): Flow<PagingData<PokemonListItemModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PokemonPagingSource(pokemonApiService) }
        ).flow
    }

    override suspend fun getPokemonDetail(pokemonIdOrName: String): PokemonDetailResult {
        return try {

            val id = pokemonIdOrName.toIntOrNull()

            // --- 1. Try DB cache ---
            val cached = when {
                // If parsing id is not possible, fallback to name
                id != null -> pokemonDetailDao.getPokemonDetailById(id).firstOrNull()
                else -> pokemonDetailDao.getPokemonDetailByName(pokemonIdOrName).firstOrNull()
            }

            if (cached != null) {
                return PokemonDetailResult.Success(cached.toUiEntity())
            }

            // 2. Fetch from API
            val response = pokemonApiService.getPokemonDetail(pokemonIdOrName)
            when (response) {
                is APIResult.Success -> {
                    val uiEntity = PokemonDetailResult.Success(response.data.toUiEntity())

                    // 3. Save to DB
                    pokemonDetailDao.insertPokemonDetail(uiEntity.pokemon.toDBEntity())

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

        // 1. Emit from DB cache first (if available)
        val cached = pokemonListDao.getAllPokemon().firstOrNull()
        if (!cached.isNullOrEmpty()) {
            emit(APIResult.Success(cached.map { it.toModel() }))
        }

        // 2. Fetch from API
        val response = try {
            pokemonApiService.getPokemonList(limit, offset)
        } catch (e: IOException) {
            emit(APIResult.Failure(e))
            return@flow
        } catch (e: HttpException) {
            emit(APIResult.Failure(e, code = e.code()))
            return@flow
        } catch (e: Exception) {
            emit(APIResult.Failure(e))
            return@flow
        }

        val items = response.results?.mapNotNull { result ->
            if (!result.name.isNullOrEmpty() && !result.url.isNullOrEmpty()) {
                PokemonListItemModel(result.name, result.url)
            } else null
        }.orEmpty()

        // 3. Save to DB
        // TODO update instead of re insert full list
        pokemonListDao.clearPokemonList()
        pokemonListDao.insertPokemonList(items.map { it.toDBEntity() })

        emit(APIResult.Success(items))
    }
}
