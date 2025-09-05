package com.maubocanegra.pokedex.pokemonlist.domain.usecase

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import com.maubocanegra.pokedex.core.domain.model.PokemonListItemModel
import com.maubocanegra.pokedex.pokemonlist.domain.repository.PokemonRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

// Helper callbacks for AsyncPagingDataDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest

class GetPokemonListPagerUseCaseTest {

    private lateinit var repository: PokemonRepository
    private lateinit var useCase: GetPokemonListPagerUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetPokemonListPagerUseCase(repository)
    }

    // Helper to collect PagingData into a list without extra libraries
    private suspend fun <T : Any> PagingData<T>.toList(): List<T> {
        val differ = AsyncPagingDataDiffer(
            diffCallback = object : DiffUtil.ItemCallback<T>() {
                override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem == newItem
                override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem == newItem
            },
            updateCallback = object : ListUpdateCallback {
                override fun onInserted(position: Int, count: Int) {}
                override fun onRemoved(position: Int, count: Int) {}
                override fun onMoved(fromPosition: Int, toPosition: Int) {}
                override fun onChanged(position: Int, count: Int, payload: Any?) {}
            },
            mainDispatcher = kotlinx.coroutines.Dispatchers.Unconfined,
            workerDispatcher = kotlinx.coroutines.Dispatchers.Unconfined
        )
        differ.submitData(this)
        // Wait until submitted
        return differ.snapshot().items
    }

    @Test
    fun `invoke returns correct PagingData items`() = runTest {
        val items = listOf(
            PokemonListItemModel("bulbasaur", "url1"),
            PokemonListItemModel("ivysaur", "url2")
        )
        val pagingData = PagingData.from(items)

        coEvery { repository.getPokemonPager(20) } returns flowOf(pagingData)

        val resultFlow: Flow<PagingData<PokemonListItemModel>> = useCase(20)
        val actualItems = resultFlow.single().toList()

        assertEquals(items, actualItems)
    }

    @Test
    fun `invoke with empty PagingData returns empty list`() = runTest {
        val pagingData = PagingData.from(emptyList<PokemonListItemModel>())
        coEvery { repository.getPokemonPager(20) } returns flowOf(pagingData)

        val actualItems = useCase(20).single().toList()

        assertEquals(0, actualItems.size)
    }
}
