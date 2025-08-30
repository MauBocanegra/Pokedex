package com.maubocanegra.pokedex.pokemonlist.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.maubocanegra.pokedex.pokemonlist.domain.model.PokemonListItemModel
import com.maubocanegra.pokedex.pokemonlist.domain.usecase.GetPokemonListPagerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PokemonListScreenViewModel @Inject constructor(
    private val getPokemonListPagerUseCase: GetPokemonListPagerUseCase
) : ViewModel() {

    private val _pokemonPagerState = MutableStateFlow(PagingData.empty<PokemonListItemModel>())
    val pokemonPagerState: StateFlow<PagingData<PokemonListItemModel>> = _pokemonPagerState

    init {
        fetchPokemonPager()
    }

    private fun fetchPokemonPager(pageSize: Int = 20) {
        viewModelScope.launch {
            getPokemonListPagerUseCase(pageSize)
                .cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _pokemonPagerState.value = pagingData
                }
        }
    }

    // Trigger refresh from Compose (pull-to-refresh)
    fun refreshList() {
        fetchPokemonPager()
    }
}
