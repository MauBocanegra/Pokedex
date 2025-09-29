package com.maubocanegra.pokedex.pokemonstaggered.data.persistence.mapper

import androidx.paging.PagingData
import androidx.paging.map
import com.maubocanegra.pokedex.pokemon.data.persistence.entity.PokemonListItemDBEntity
import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonUiEntity
import com.maubocanegra.pokedex.pokemonlist.data.network.model.PokemonListResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun PokemonListResponse.toDBEntities(): List<PokemonListItemDBEntity> {
    return results.orEmpty().mapNotNull { resource ->
        val name = resource.name ?: return@mapNotNull null
        val url = resource.url ?: return@mapNotNull null
        val id = url.trimEnd('/').substringAfterLast('/').toIntOrNull() ?: return@mapNotNull null
        PokemonListItemDBEntity(id = id, name = name, url = url)
    }
}

fun PokemonListItemDBEntity.toUiEntity(): PokemonUiEntity = PokemonUiEntity(
    id = id,
    name = name,
    url = url,
    baseExperience = null,
    height = null,
    order = null,
    weight = null,
    abilities = null,
    forms = null,
    locationAreaEncounters = null,
    moves = null,
    sprites = null,
    stats = null,
    types = null,
    officialArtwork = null,
    criesLatest = null
)

/** DB PagingData -> UI PagingData */
fun PagingData<PokemonListItemDBEntity>.toUiPaging(): PagingData<PokemonUiEntity> =
    map { it.toUiEntity() }

/** Flow<DB PagingData> -> Flow<UI PagingData> */
fun Flow<PagingData<PokemonListItemDBEntity>>.toUiFlow(): Flow<PagingData<PokemonUiEntity>> =
    map { it.toUiPaging() }