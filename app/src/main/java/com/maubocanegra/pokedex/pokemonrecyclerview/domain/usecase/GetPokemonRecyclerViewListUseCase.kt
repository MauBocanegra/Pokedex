package com.maubocanegra.pokedex.pokemonrecyclerview.domain.usecase

import com.maubocanegra.pokedex.core.domain.model.PokemonListItemModel
import com.maubocanegra.pokedex.core.network.util.APIResult
import com.maubocanegra.pokedex.pokemon.domain.repository.PokemonRepository
import com.maubocanegra.pokedex.pokemonrecyclerview.domain.mapper.mapPokemonRecyclerViewResponseToUiState
import com.maubocanegra.pokedex.pokemonrecyclerview.domain.uistate.PokemonRecyclerViewUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetPokemonRecyclerViewUiStateUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    suspend operator fun invoke(limit: Int = 20, offset: Int = 0): Flow<PokemonRecyclerViewUiState> {
        return repository.getPokemonList(limit, offset)
            .map { apiResult: APIResult<List<PokemonListItemModel>> ->
                mapPokemonRecyclerViewResponseToUiState(apiResult)
            }
            .flowOn(Dispatchers.IO)
    }
}
