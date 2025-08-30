package com.maubocanegra.pokedex.core.network

import com.maubocanegra.pokedex.pokemonlist.data.network.model.PokemonListResponse
import com.maubocanegra.pokedex.pokemonlist.data.network.model.PokemonResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface API {
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ):Response<PokemonListResponse>

    @GET("pokemon/{idOrName}")
    suspend fun getPokemonDetail(
        @Path("idOrName") idOrName: String
    ): Response<PokemonResponse>
}
