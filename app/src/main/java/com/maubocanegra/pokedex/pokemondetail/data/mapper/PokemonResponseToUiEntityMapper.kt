package com.maubocanegra.pokedex.pokemondetail.data.mapper

import com.maubocanegra.pokedex.pokemondetail.domain.entity.CustomPokemonAPIResource
import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonAbilityUiEntity
import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonMoveUiEntity
import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonSpriteUiEntity
import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonStatUiEntity
import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonTypesUiEntity
import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonUiEntity
import com.maubocanegra.pokedex.pokemonlist.data.network.model.AbilityResponse
import com.maubocanegra.pokedex.pokemonlist.data.network.model.MoveResponse
import com.maubocanegra.pokedex.pokemonlist.data.network.model.NamedAPIResource
import com.maubocanegra.pokedex.pokemonlist.data.network.model.PokemonResponse
import com.maubocanegra.pokedex.pokemonlist.data.network.model.SpritesResponse
import com.maubocanegra.pokedex.pokemonlist.data.network.model.StatResponse
import com.maubocanegra.pokedex.pokemonlist.data.network.model.TypeResponse

fun PokemonResponse.toUiEntity(): PokemonUiEntity {
    return PokemonUiEntity(
        id = this.id,
        url = "https://pokeapi.co/api/v2/pokemon/${this.id}/",
        name = this.name,
        baseExperience = this.baseExperience,
        height = this.height,
        order = this.order,
        weight = this.weight,
        abilities = this.abilities?.map { it.toUiAbilityEntity() } ?: emptyList(),
        forms = this.forms?.map { it.toCustomResource() },
        locationAreaEncounters = this.locationAreaEncounters,
        moves = this.moves?.map { it.toMoveUiEntity() },
        sprites = this.sprites?.toSpriteUiEntity()?.let { listOf(it) },
        stats = this.stats?.map { it.toStatUiEntity() },
        types = this.types?.map { it.toTypeUiEntity() } ?: emptyList(),
        officialArtwork = this.sprites?.other?.officialArtwork?.frontDefault,
        criesLatest = this.cries?.latest
    )
}

// --- Nested Mappers ---

private fun AbilityResponse.toUiAbilityEntity(): PokemonAbilityUiEntity {
    return PokemonAbilityUiEntity(
        isHidden = this.isHidden,
        slot = this.slot,
        ability = this.ability.toCustomResource()
    )
}

private fun NamedAPIResource.toCustomResource(): CustomPokemonAPIResource {
    return CustomPokemonAPIResource(
        name = this.name,
        url = this.url
    )
}

private fun MoveResponse.toMoveUiEntity(): PokemonMoveUiEntity {
    return PokemonMoveUiEntity(
        move = this.move.toCustomResource()
    )
}

private fun StatResponse.toStatUiEntity(): PokemonStatUiEntity {
    return PokemonStatUiEntity(
        baseStat = this.baseStat,
        effort = this.effort,
        stat = this.stat.toCustomResource()
    )
}

private fun TypeResponse.toTypeUiEntity(): PokemonTypesUiEntity {
    return PokemonTypesUiEntity(
        slot = this.slot,
        type = this.type.toCustomResource()
    )
}

private fun SpritesResponse.toSpriteUiEntity(): PokemonSpriteUiEntity {
    return PokemonSpriteUiEntity(
        backDefault = this.backDefault,
        backShiny = this.backShiny,
        frontDefault = this.frontDefault
    )
}
