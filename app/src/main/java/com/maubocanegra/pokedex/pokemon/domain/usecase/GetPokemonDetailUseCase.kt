package com.maubocanegra.pokedex.pokemon.domain.usecase

import com.maubocanegra.pokedex.pokemondetail.domain.mapper.toPokemonDetailUiState
import com.maubocanegra.pokedex.pokemondetail.domain.model.PokemonDetailResult
import com.maubocanegra.pokedex.pokemondetail.domain.uistate.PokemonDetailUiState
import com.maubocanegra.pokedex.pokemonlist.domain.model.UIState
import com.maubocanegra.pokedex.pokemon.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetPokemonDetailUseCase @Inject constructor(
    private val repository: PokemonRepository
) {

    operator fun invoke(pokemonId: Int): Flow<PokemonDetailUiState> = flow {
        // Emit loading state first
        emit(PokemonDetailUiState(uiState = UIState.LOADING))

        try {
            val pokemonDetailResult = repository.getPokemonDetail(pokemonId)
            when (pokemonDetailResult) {
                is PokemonDetailResult.Success -> emit(pokemonDetailResult.toPokemonDetailUiState())
                is PokemonDetailResult.Error -> emit(pokemonDetailResult.toPokemonDetailUiState())
            }
        } catch (e: Throwable) {
            val result = PokemonDetailResult.Error(e)
            emit(result.toPokemonDetailUiState())
        }
    }
}
