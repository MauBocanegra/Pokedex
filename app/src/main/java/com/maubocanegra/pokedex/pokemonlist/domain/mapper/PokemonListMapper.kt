package com.maubocanegra.pokedex.pokemonlist.domain.mapper

import com.maubocanegra.pokedex.pokemonlist.data.network.model.PokemonListResponse
import com.maubocanegra.pokedex.core.domain.model.PokemonListItemModel
import com.maubocanegra.pokedex.core.domain.model.PokemonListModel

object PokemonListMapper {

    fun mapToPokemonListModel(
        response: PokemonListResponse
    ): PokemonListModel {
        val items = response.results?.mapNotNull {
            val id = it.url?.trimEnd('/')?.substringAfterLast('/')?.toInt()
            val name = it.name
            val url = it.url
            if(!name.isNullOrBlank() && !url.isNullOrBlank()){
                PokemonListItemModel(id ?: 0, name, url)
            } else {
                null
            }
        } ?: emptyList()

        return PokemonListModel(results = items)
    }
}