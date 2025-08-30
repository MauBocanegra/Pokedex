package com.maubocanegra.pokedex.pokemonlist.data.network.datasource

import androidx.paging.PagingSource.LoadParams
import androidx.paging.PagingSource.LoadResult
import com.maubocanegra.pokedex.pokemonlist.data.network.PokemonApiService
import com.maubocanegra.pokedex.pokemonlist.data.network.model.PokemonListResponse
import com.maubocanegra.pokedex.pokemonlist.data.network.model.PokemonURLResponse
import com.maubocanegra.pokedex.pokemonlist.domain.mapper.PokemonListMapper
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class PokemonPagingSourceTest {

    private lateinit var apiService: PokemonApiService
    private lateinit var pagingSource: PokemonPagingSource

    @Before
    fun setup() {
        apiService = mockk()
        pagingSource = PokemonPagingSource(apiService)
    }

    @Test
    fun `load returns Page on successful response`() = runBlocking {
        // ---
        val mockResponse = PokemonListResponse(
            count = 2,
            next = "https://pokeapi.co/api/v2/pokemon?offset=2&limit=2",
            previous = null,
            results = listOf(
                PokemonURLResponse(name = "bulbasaur", url = "url1"),
                PokemonURLResponse(name = "ivysaur", url = "url2")
            )
        )
        coEvery { apiService.getPokemonList(2, 0) } returns mockResponse

        // ---
        val result = pagingSource.load(
            LoadParams.Refresh(
                key = null,
                loadSize = 2,
                placeholdersEnabled = false
            )
        )

        // ---
        val expected = LoadResult.Page(
            data = PokemonListMapper.mapToPokemonListModel(mockResponse).results,
            prevKey = null,
            nextKey = 2
        )
        assertEquals(expected, result)
    }

    @Test
    fun `load returns Error on http exception`() = runBlocking {
        // ---
        val errorResponse = Response.error<PokemonListResponse>(
            404,
            ResponseBody.create("application/json".toMediaTypeOrNull(), """{"error":"Not found"}""")
        )
        val httpException = HttpException(errorResponse)
        coEvery { apiService.getPokemonList(2, 0) } throws httpException

        // ---
        val result = pagingSource.load(
            LoadParams.Refresh(
                key = null,
                loadSize = 2,
                placeholdersEnabled = false
            )
        )

        // ---
        val errorResult = result as LoadResult.Error
        assert(errorResult.throwable is HttpException)
        assertEquals(404, (errorResult.throwable as HttpException).code())
    }

    @Test
    fun `load returns Page with no nextKey on empty results`() = runBlocking {
        // ---
        val emptyResponse = PokemonListResponse(results = emptyList())
        coEvery { apiService.getPokemonList(2, 0) } returns emptyResponse

        // ---
        val result = pagingSource.load(
            LoadParams.Refresh(
                key = null,
                loadSize = 2,
                placeholdersEnabled = false
            )
        )

        // ---
        val page = result as LoadResult.Page
        assertTrue(page.data.isEmpty())
        assertEquals(null, page.prevKey)
        // nextKey should be null when no more data
        assertEquals(null, page.nextKey)
    }
}