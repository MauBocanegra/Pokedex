package com.maubocanegra.pokedex.pokemonlist.domain.usecase

import androidx.paging.PagingData
import com.maubocanegra.pokedex.core.domain.model.PokemonListItemModel
import com.maubocanegra.pokedex.pokemonlist.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPokemonListPagerUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    operator fun invoke(pageSize: Int = 20): Flow<PagingData<PokemonListItemModel>> {
        return repository.getPokemonPager(pageSize)
    }
}