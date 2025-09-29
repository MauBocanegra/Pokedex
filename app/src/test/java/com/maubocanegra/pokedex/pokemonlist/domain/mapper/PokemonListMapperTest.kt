package com.maubocanegra.pokedex.pokemonlist.domain.mapper

import com.maubocanegra.pokedex.pokemonlist.data.network.model.PokemonListResponse
import com.maubocanegra.pokedex.pokemonlist.data.network.model.PokemonURLResponse
import com.maubocanegra.pokedex.core.domain.model.PokemonListItemModel
import com.maubocanegra.pokedex.core.domain.model.PokemonListModel
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PokemonListMapperTest {

    // Unit testing ---

    @Test
    fun `mapToPokemonListModel maps valid results`() {
        val response = PokemonListResponse(
            results = listOf(
                PokemonURLResponse(name = "bulbasaur", url = "url1"),
                PokemonURLResponse(name = "ivysaur", url = "url2")
            )
        )

        val result: PokemonListModel = PokemonListMapper.mapToPokemonListModel(response)

        assertEquals(2, result.results.size)
        assertEquals(PokemonListItemModel(1, "bulbasaur", "url1"), result.results[0])
        assertEquals(PokemonListItemModel(2, "ivysaur", "url2"), result.results[1])
    }

    @Test
    fun `mapToPokemonListModel ignores null name or url`() {
        val response = PokemonListResponse(
            results = listOf(
                PokemonURLResponse(name = "bulbasaur", url = "url1"),
                PokemonURLResponse(name = null, url = "url2"),
                PokemonURLResponse(name = "ivysaur", url = null)
            )
        )

        val result: PokemonListModel = PokemonListMapper.mapToPokemonListModel(response)

        // Only the first should be valid
        assertEquals(1, result.results.size)
        assertEquals("bulbasaur", result.results[0].name)
        assertEquals("url1", result.results[0].url)
    }

    @Test
    fun `mapToPokemonListModel returns empty list when results is null`() {
        val response = PokemonListResponse(results = null)

        val result: PokemonListModel = PokemonListMapper.mapToPokemonListModel(response)

        assertTrue(result.results.isEmpty())
    }

    @Test
    fun `mapToPokemonListModel returns empty list when results is empty`() {
        val response = PokemonListResponse(results = emptyList())

        val result: PokemonListModel = PokemonListMapper.mapToPokemonListModel(response)

        assertTrue(result.results.isEmpty())
    }

    // Property-based tests (Integration-ish tests) ---

    @Test
    fun `mapToPokemonListModel never returns null items`(): Unit = runBlocking {
        val arbPokemonUrlResponse = Arb.bind(
            Arb.string(minSize = 0, maxSize = 10).orNull(),
            Arb.string(minSize = 0, maxSize = 10).orNull()
        ) { name, url -> PokemonURLResponse(name, url) }

        checkAll(50, Arb.list(arbPokemonUrlResponse, 0..20)) { randomList ->
            val response = PokemonListResponse(results = randomList)
            val result = PokemonListMapper.mapToPokemonListModel(response)

            // Mapped items should never contain null name or url
            assertTrue(result.results.all { it.name.isNotEmpty() && it.url.isNotEmpty() })
        }
    }

    @Test
    fun `mapToPokemonListModel size never exceeds input size`(): Unit = runBlocking {
        val arbPokemonUrlResponse = Arb.bind(
            Arb.string(minSize = 0, maxSize = 10).orNull(),
            Arb.string(minSize = 0, maxSize = 10).orNull()
        ) { name, url -> PokemonURLResponse(name, url) }

        checkAll(50, Arb.list(arbPokemonUrlResponse, 0..20)) { randomList ->
            val response = PokemonListResponse(results = randomList)
            val result = PokemonListMapper.mapToPokemonListModel(response)

            // Resulting list should be lesser than input list as invalid entries are filtered
            assertTrue(result.results.size <= (response.results?.size ?: 0))
        }
    }
}