package com.maubocanegra.pokedex.pokemon.data.persistence.mapper

import com.maubocanegra.pokedex.core.domain.model.PokemonListItemModel
import com.maubocanegra.pokedex.pokemon.data.persistence.converter.PokemonTypeConverters
import com.maubocanegra.pokedex.pokemon.data.persistence.entity.PokemonDetailDBEntity
import com.maubocanegra.pokedex.pokemon.data.persistence.entity.PokemonDreamWorldSpritesEmbeddable
import com.maubocanegra.pokedex.pokemon.data.persistence.entity.PokemonHomeSpritesEmbeddable
import com.maubocanegra.pokedex.pokemon.data.persistence.entity.PokemonListItemDBEntity
import com.maubocanegra.pokedex.pokemon.data.persistence.entity.PokemonSpritesEmbeddable
import com.maubocanegra.pokedex.pokemondetail.domain.entity.OtherSpritesUiEntity
import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonDreamWorldSpriteUIEntity
import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonHomeSpriteUIEntity
import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonSpriteUiEntity
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
    sprites = sprites.toDBEntity(),
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
    sprites = sprites?.toUiEntity(),
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
        frontSpriteUrl = "",
    )

fun PokemonListItemModel.toDBEntity(): PokemonListItemDBEntity =
    PokemonListItemDBEntity(
        id = url.trimEnd('/').substringAfterLast('/').toInt(),
        name = name,
        url = url,
        types = types,
        frontSpriteUrl = "",
    )

fun PokemonDetailDBEntity.toListDBEntity(): PokemonListItemDBEntity =
    PokemonListItemDBEntity(
        id = id,
        name = name ?: "",
        url = url ?: "",
        types = types,
        frontSpriteUrl = "",
    )

// -----------------------------
// Sprites mapper
// -----------------------------

fun PokemonSpritesEmbeddable.toUiEntity(): PokemonSpriteUiEntity =
    PokemonSpriteUiEntity(
        backDefault, backShiny, frontDefault, backFemale, backShinyFemale, frontFemale, frontShiny,
        other = OtherSpritesUiEntity(
            homeSprites =  PokemonHomeSpriteUIEntity(
                this.home?.homeFrontDefault,
                this.home?.homeFrontFemale
            ) ,
            dreamWorldSprites = PokemonDreamWorldSpriteUIEntity(
                this.dreamWorld?.dreamWorldFrontDefault,
                this.dreamWorld?.dreamWorldFrontFemale
            )
        )
    )

fun PokemonSpriteUiEntity?.toDBEntity(): PokemonSpritesEmbeddable =
    PokemonSpritesEmbeddable(
        this?.backDefault,
        this?.backShiny,
        this?.frontDefault,
        this?.backFemale,
        this?.backShinyFemale,
        this?.frontFemale,
        this?.frontShiny,
        home = this?.other?.homeSprites?.let {
            PokemonHomeSpritesEmbeddable(
                homeFrontDefault = it.homeFrontDefault,
                homeFrontFemale = it.homeFrontFemale
            )
        },
        dreamWorld = this?.other?.dreamWorldSprites?.let {
            PokemonDreamWorldSpritesEmbeddable(
                dreamWorldFrontDefault = it.dreamWorldFrontDefault,
                dreamWorldFrontFemale = it.dreamWorldFrontFemale,
            )
        }
    )