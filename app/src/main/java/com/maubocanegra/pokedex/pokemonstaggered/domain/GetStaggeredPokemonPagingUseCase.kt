package com.maubocanegra.pokedex.pokemonstaggered.domain

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.maubocanegra.pokedex.pokemon.domain.repository.PokemonRepository
import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonUiEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStaggeredPokemonPagingUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    operator fun invoke(): Flow<PagingData<PokemonUiEntity>> =
        repository.pagingFlow(
            PagingConfig(
                pageSize = 20,
                initialLoadSize = 40,        // 2 × pageSize
                prefetchDistance = 20,       // 1 × pageSize
                enablePlaceholders = false
            )
        )
}