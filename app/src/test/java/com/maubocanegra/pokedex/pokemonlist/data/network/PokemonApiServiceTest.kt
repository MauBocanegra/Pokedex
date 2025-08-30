package com.maubocanegra.pokedex.pokemonlist.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.maubocanegra.pokedex.core.network.API
import com.maubocanegra.pokedex.pokemonlist.data.network.model.PokemonListResponse
import com.maubocanegra.pokedex.pokemonlist.data.network.model.PokemonURLResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit

class PokemonApiServiceTest {
    // --- MockWebServer tests (integration-testing)
    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: API
    private lateinit var apiService: PokemonApiService

    // --- mockk tests (unit-testing)
    private lateinit var mockApi: API
    private lateinit var mockApiService: PokemonApiService

    @Before
    fun setup() {
        // setup integration api and service
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val contentType = "application/json".toMediaType()
        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
            .create(API::class.java)

        apiService = PokemonApiService(api)

        // setup mockk api and service
        mockApi = mockk()
        mockApiService = PokemonApiService(mockApi)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    // --- MockWebServer tests ---
    @Test
    fun `getPokemonList returns valid response from server`() = runBlocking {
        val jsonResponse = """
            {
              "count": 2,
              "next": null,
              "previous": null,
              "results": [
                { "name": "bulbasaur", "url": "https://pokeapi.co/api/v2/pokemon/1/" },
                { "name": "ivysaur", "url": "https://pokeapi.co/api/v2/pokemon/2/" }
              ]
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        val result = apiService.getPokemonList(limit = 20, offset = 0)

        assertEquals(2, result.results?.size)
        assertEquals("bulbasaur", result.results?.get(0)?.name)
        assertEquals("ivysaur", result.results?.get(1)?.name)
    }

    @Test(expected = HttpException::class)
    fun `getPokemonList throws HttpException on server error`(): Unit = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
        )

        apiService.getPokemonList(limit = 20, offset = 0)
    }

    // --- Mockk tests ---
    @Test
    fun `getPokemonList returns valid data response`() = runBlocking {
        val mockResponse = PokemonListResponse(
            count = 1,
            results = listOf(PokemonURLResponse(name = "bulbasaur", url = "url1"))
        )
        val retrofitResponse = Response.success(mockResponse)

        coEvery { mockApi.getPokemonList(20, 0) } returns retrofitResponse

        val result = mockApiService.getPokemonList(20, 0)

        assertEquals(mockResponse, result)
    }

    @Test(expected = HttpException::class)
    fun `getPokemonList throws HttpException when response fails`(): Unit = runBlocking {
        val errorResponse = Response.error<PokemonListResponse>(
            404,
            "{}".toResponseBody("application/json".toMediaType())
        )

        coEvery { mockApi.getPokemonList(20, 0) } returns errorResponse

        mockApiService.getPokemonList(20, 0)
    }

    @Test(expected = Exception::class)
    fun `getPokemonList throws Exception when body is null`(): Unit = runBlocking {
        val retrofitResponse = Response.success<PokemonListResponse>(null)

        coEvery { mockApi.getPokemonList(20, 0) } returns retrofitResponse

        mockApiService.getPokemonList(20, 0)
    }
}