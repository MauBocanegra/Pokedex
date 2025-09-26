package com.maubocanegra.pokedex.pokemonrecyclerview.domain.mapper

import com.maubocanegra.pokedex.core.domain.model.PokemonListItemModel
import com.maubocanegra.pokedex.core.network.util.APIResult
import com.maubocanegra.pokedex.pokemon.data.persistence.converter.PokemonTypeConverters.toTypesList
import com.maubocanegra.pokedex.pokemonrecyclerview.domain.uistate.PokemonRecyclerViewUiState
import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonUiEntity
import com.maubocanegra.pokedex.pokemonlist.domain.model.UIState

fun mapPokemonRecyclerViewResponseToUiState(
    apiResult: APIResult<List<PokemonListItemModel>>
): PokemonRecyclerViewUiState {
    return when (apiResult) {
        is APIResult.Loading -> {
            PokemonRecyclerViewUiState(
                pokemonList = emptyList(),
                uiState = UIState.LOADING
            )
        }
        // TODO mapper not finished
        is APIResult.Success -> {
            val uiList = apiResult.data.map { item ->
                PokemonUiEntity(
                    id = extractIdFromUrl(item.url), // ???
                    name = item.name,
                    url = item.url,
                    baseExperience = null,
                    height = null,
                    order = null,
                    weight = null,
                    abilities = emptyList(),
                    forms = null,
                    locationAreaEncounters = null,
                    moves = null,
                    sprites = null,
                    stats = null,
                    types = toTypesList(item.types),
                    officialArtwork = null,
                    criesLatest = null
                )
            }
            PokemonRecyclerViewUiState(
                pokemonList = uiList,
                uiState = UIState.SUCCESS
            )
        }
        is APIResult.Failure -> {
            PokemonRecyclerViewUiState(
                pokemonList = emptyList(),
                uiState = UIState.FAILED,
                errorMessage = apiResult.message ?: "Unknown error"
            )
        }
    }
}

/** Helper to extract numeric ID from Pokémon URL, e.g., "https://pokeapi.co/api/v2/pokemon/1/" -> 1 */
private fun extractIdFromUrl(url: String?): Int {
    return url?.trimEnd('/')?.split("/")?.lastOrNull()?.toIntOrNull() ?: 0
}