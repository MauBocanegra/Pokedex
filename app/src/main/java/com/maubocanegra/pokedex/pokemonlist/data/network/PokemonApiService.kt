package com.maubocanegra.pokedex.pokemonlist.data.network

import com.maubocanegra.pokedex.core.network.API
import com.maubocanegra.pokedex.core.network.util.APIResult
import com.maubocanegra.pokedex.pokemonlist.data.network.model.PokemonListResponse
import com.maubocanegra.pokedex.pokemonlist.data.network.model.PokemonResponse
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class PokemonApiService @Inject constructor(
    private val api: API,
) {
    suspend fun getPokemonList(limit: Int = 20, offset: Int = 0): PokemonListResponse {
        val response = api.getPokemonList(limit, offset)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Response body is null")
        } else {
            throw HttpException(response)
        }
    }

    suspend fun getPokemonDetail(id: Int): APIResult<PokemonResponse> {
        return try {
            val response = api.getPokemonDetail(id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    APIResult.Success(body)
                } else {
                    APIResult.Failure(
                        throwable = NullPointerException("Response body is null")
                    )
                }
            } else {
                APIResult.Failure(
                    throwable = HttpException(response),
                    code = response.code()
                )
            }
        } catch (e: IOException) {
            // Network or conversion error
            APIResult.Failure(e)
        } catch (e: HttpException) {
            APIResult.Failure(e, code = e.code())
        } catch (e: Exception) {
            APIResult.Failure(e)
        }
    }
}
