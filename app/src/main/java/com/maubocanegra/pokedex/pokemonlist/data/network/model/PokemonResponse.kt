package com.maubocanegra.pokedex.pokemonlist.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PokemonResponse(
    val id: Int,
    val name: String,
    @SerialName("base_experience") val baseExperience: Int? = null,
    val height: Int? = null,
    @SerialName("is_default") val isDefault: Boolean? = null,
    val order: Int? = null,
    val weight: Int? = null,
    val abilities: List<AbilityResponse>? = null,
    val forms: List<NamedAPIResource>? = null,
    @SerialName("game_indices") val gameIndices: List<GameIndexResponse>? = null,
    @SerialName("held_items") val heldItems: List<HeldItemResponse>? = null,
    @SerialName("location_area_encounters") val locationAreaEncounters: String? = null,
    val moves: List<MoveResponse>? = null,
    val species: NamedAPIResource? = null,
    val sprites: SpritesResponse? = null,
    val cries: CriesResponse? = null,
    val stats: List<StatResponse>? = null,
    val types: List<TypeResponse>? = null,
    @SerialName("past_types") val pastTypes: List<PastTypeResponse>? = null,
    @SerialName("past_abilities") val pastAbilities: List<PastAbilityResponse>? = null,
)

@Serializable
data class AbilityResponse(
    @SerialName("is_hidden") val isHidden: Boolean,
    val slot: Int,
    val ability: NamedAPIResource
)

@Serializable
data class GameIndexResponse(
    @SerialName("game_index") val gameIndex: Int,
    val version: NamedAPIResource
)

@Serializable
data class HeldItemResponse(
    val item: NamedAPIResource,
    @SerialName("version_details") val versionDetails: List<VersionDetailResponse>
)

@Serializable
data class VersionDetailResponse(
    val rarity: Int,
    val version: NamedAPIResource
)

@Serializable
data class MoveResponse(
    val move: NamedAPIResource,
    @SerialName("version_group_details") val versionGroupDetails: List<VersionGroupDetailResponse>
)

@Serializable
data class VersionGroupDetailResponse(
    @SerialName("level_learned_at") val levelLearnedAt: Int,
    @SerialName("version_group") val versionGroup: NamedAPIResource,
    @SerialName("move_learn_method") val moveLearnMethod: NamedAPIResource,
    val order: Int? = null
)

@Serializable
data class SpritesResponse(
    @SerialName("back_default") val backDefault: String? = null,
    @SerialName("back_female") val backFemale: String? = null,
    @SerialName("back_shiny") val backShiny: String? = null,
    @SerialName("back_shiny_female") val backShinyFemale: String? = null,
    @SerialName("front_default") val frontDefault: String? = null,
    @SerialName("front_female") val frontFemale: String? = null,
    @SerialName("front_shiny") val frontShiny: String? = null,
    @SerialName("front_shiny_female") val frontShinyFemale: String? = null,
    val other: OtherSpritesResponse? = null
)

@Serializable
data class OtherSpritesResponse(
    @SerialName("dream_world") val dreamWorld: DreamWorldSprite? = null,
    val home: HomeSprite? = null,
    @SerialName("official-artwork") val officialArtwork: OfficialArtworkSprite? = null,
    val showdown: ShowdownSprite? = null
)

@Serializable
data class DreamWorldSprite(
    @SerialName("front_default") val frontDefault: String? = null,
    @SerialName("front_female") val frontFemale: String? = null
)

@Serializable
data class HomeSprite(
    @SerialName("front_default") val frontDefault: String? = null,
    @SerialName("front_female") val frontFemale: String? = null,
    @SerialName("front_shiny") val frontShiny: String? = null,
    @SerialName("front_shiny_female") val frontShinyFemale: String? = null
)

@Serializable
data class OfficialArtworkSprite(
    @SerialName("front_default") val frontDefault: String? = null,
    @SerialName("front_shiny") val frontShiny: String? = null
)

@Serializable
data class ShowdownSprite(
    @SerialName("back_default") val backDefault: String? = null,
    @SerialName("back_female") val backFemale: String? = null,
    @SerialName("back_shiny") val backShiny: String? = null,
    @SerialName("back_shiny_female") val backShinyFemale: String? = null,
    @SerialName("front_default") val frontDefault: String? = null,
    @SerialName("front_female") val frontFemale: String? = null,
    @SerialName("front_shiny") val frontShiny: String? = null,
    @SerialName("front_shiny_female") val frontShinyFemale: String? = null
)

@Serializable
data class CriesResponse(
    val latest: String? = null,
    val legacy: String? = null
)

@Serializable
data class StatResponse(
    @SerialName("base_stat") val baseStat: Int,
    val effort: Int,
    val stat: NamedAPIResource
)

@Serializable
data class TypeResponse(
    val slot: Int,
    val type: NamedAPIResource
)

@Serializable
data class PastTypeResponse(
    val generation: NamedAPIResource,
    val types: List<TypeResponse>
)

@Serializable
data class PastAbilityResponse(
    val generation: NamedAPIResource,
    val abilities: List<PastAbilityDetail>
)

@Serializable
data class PastAbilityDetail(
    val ability: NamedAPIResource? = null,
    @SerialName("is_hidden") val isHidden: Boolean,
    val slot: Int
)

@Serializable
data class NamedAPIResource(
    val name: String,
    val url: String
)