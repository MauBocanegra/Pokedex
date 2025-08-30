package com.maubocanegra.pokedex.pokemondetail.domain.mapper


import com.maubocanegra.pokedex.pokemondetail.domain.model.PokemonDetailResult
import com.maubocanegra.pokedex.pokemondetail.domain.uistate.PokemonDetailUiState
import com.maubocanegra.pokedex.pokemonlist.domain.model.UIState

fun PokemonDetailResult.toPokemonDetailUiState(): PokemonDetailUiState = when (this) {
    is PokemonDetailResult.Success -> PokemonDetailUiState(
        pokemon = this.pokemon,
        uiState = UIState.SUCCESS
    )

    is PokemonDetailResult.Error -> PokemonDetailUiState(
        uiState = UIState.FAILED,
        errorMessage = this.message ?: this.throwable.localizedMessage
    )
}