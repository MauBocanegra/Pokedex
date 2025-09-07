package com.maubocanegra.pokedex.pokemon.data.persistence.converter

import androidx.room.TypeConverter
import com.maubocanegra.pokedex.pokemondetail.domain.entity.CustomPokemonAPIResource
import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonAbilityUiEntity
import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonMoveUiEntity
import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonSpriteUiEntity
import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonStatUiEntity
import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonTypesUiEntity
import kotlinx.serialization.json.Json

object PokemonTypeConverters {

    // Single Json instance configured to tolerate missing fields
    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    /**----- Pokemon types -----*/
    @TypeConverter
    @JvmStatic
    fun fromTypesList(types: List<PokemonTypesUiEntity>?): String? =
        types?.let { json.encodeToString(it) }


    @TypeConverter
    @JvmStatic
    fun toTypesList(jsonString: String?): List<PokemonTypesUiEntity>? =
        jsonString?.let { json.decodeFromString(it) }

    /**----- Pokemon abilities -----*/
    @TypeConverter
    @JvmStatic
    fun fromAbilities(abilities: List<PokemonAbilityUiEntity>?): String? =
        abilities?.let { json.encodeToString(it) }

    @TypeConverter
    @JvmStatic
    fun toAbilities(jsonString: String?): List<PokemonAbilityUiEntity>? =
        jsonString?.let { json.decodeFromString(it) }

    /**----- Pokemon Forms -----*/
    @TypeConverter
    @JvmStatic
    fun fromForms(forms: List<CustomPokemonAPIResource>?): String? =
        forms?.let { json.encodeToString(it) }

    @TypeConverter
    @JvmStatic
    fun toForms(jsonString: String?): List<CustomPokemonAPIResource>? =
        jsonString?.let { json.decodeFromString(it) }

    /**----- Pokemon Moves -----*/
    @TypeConverter
    @JvmStatic
    fun fromMoves(moves: List<PokemonMoveUiEntity>?): String? =
        moves?.let { json.encodeToString(it) }

    @TypeConverter
    @JvmStatic
    fun toMoves(jsonString: String?): List<PokemonMoveUiEntity>? =
        jsonString?.let { json.decodeFromString(it) }

    /**----- Pokemon Sprites -----*/
    @TypeConverter
    @JvmStatic
    fun fromSprites(sprites: List<PokemonSpriteUiEntity>?): String? =
        sprites?.let { json.encodeToString(it) }

    @TypeConverter
    @JvmStatic
    fun toSprites(jsonString: String?): List<PokemonSpriteUiEntity>? =
        jsonString?.let { json.decodeFromString(it) }

    /**----- Pokemon Stats -----*/
    @TypeConverter
    @JvmStatic
    fun fromStats(stats: List<PokemonStatUiEntity>?): String? =
        stats?.let { json.encodeToString(it) }

    @TypeConverter
    @JvmStatic
    fun toStats(jsonString: String?): List<PokemonStatUiEntity>? =
        jsonString?.let { json.decodeFromString(it) }
}