package com.maubocanegra.pokedex.pokemon.data.persistence.mapper

import com.maubocanegra.pokedex.core.domain.model.PokemonListItemModel
import com.maubocanegra.pokedex.pokemon.data.persistence.converter.PokemonTypeConverters
import com.maubocanegra.pokedex.pokemon.data.persistence.entity.PokemonDetailDBEntity
import com.maubocanegra.pokedex.pokemon.data.persistence.entity.PokemonListItemDBEntity
import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonUiEntity

// -----------------------------
// UI -> Entity
// -----------------------------
fun PokemonUiEntity.toDBEntity(): PokemonDetailDBEntity = PokemonDetailDBEntity(
    id = id,
    name = name,
    url = url,
    baseExperience = baseExperience,
    height = height,
    order = order,
    weight = weight,
    abilities = PokemonTypeConverters.fromAbilities(abilities),
    forms = PokemonTypeConverters.fromForms(forms),
    locationAreaEncounters = locationAreaEncounters,
    moves = PokemonTypeConverters.fromMoves(moves),
    sprites = PokemonTypeConverters.fromSprites(sprites),
    stats = PokemonTypeConverters.fromStats(stats),
    types = PokemonTypeConverters.fromTypesList(types),
    officialArtwork = officialArtwork,
    criesLatest = criesLatest
)

// -----------------------------
// Entity -> UI
// -----------------------------
fun PokemonDetailDBEntity.toUiEntity(): PokemonUiEntity = PokemonUiEntity(
    id = id,
    name = name,
    url = url,
    baseExperience = baseExperience,
    height = height,
    order = order,
    weight = weight,
    abilities = PokemonTypeConverters.toAbilities(abilities),
    forms = PokemonTypeConverters.toForms(forms),
    locationAreaEncounters = locationAreaEncounters,
    moves = PokemonTypeConverters.toMoves(moves),
    sprites = PokemonTypeConverters.toSprites(sprites),
    stats = PokemonTypeConverters.toStats(stats),
    types = PokemonTypeConverters.toTypesList(types),
    officialArtwork = officialArtwork,
    criesLatest = criesLatest
)

// -----------------------------
// List Item Mapper
// -----------------------------
fun PokemonListItemDBEntity.toModel(): PokemonListItemModel =
    PokemonListItemModel(
        id = id,
        name = name,
        url = url,
        types = types,
        frontSpriteUrl = frontSpriteUrl,
    )

fun PokemonListItemModel.toDBEntity(): PokemonListItemDBEntity =
    PokemonListItemDBEntity(
        id = url.trimEnd('/').substringAfterLast('/').toInt(),
        name = name,
        url = url,
        types = types,
        frontSpriteUrl = frontSpriteUrl
    )

fun PokemonDetailDBEntity.toListDBEntity(): PokemonListItemDBEntity =
    PokemonListItemDBEntity(
        id = id,
        name = name ?: "",
        url = url ?: "",
        types = types,
        frontSpriteUrl = sprites
    )