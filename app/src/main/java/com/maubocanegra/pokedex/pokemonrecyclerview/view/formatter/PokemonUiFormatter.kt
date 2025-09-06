package com.maubocanegra.pokedex.pokemonrecyclerview.view.formatter

import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonTypesUiEntity
import java.util.Locale
import androidx.core.graphics.toColorInt

fun String.formatPokedexId(placeholder: String): String = when(this.length){
    1 -> "#000$this"
    2 -> "#00$this"
    3 -> "#0$this"
    else -> "#$this"
}.ifEmpty { placeholder }

fun String?.formatPokemonName(): String = this?.replaceFirstChar {
    if (it.isLowerCase())
        it.titlecase(Locale.getDefault())
    else
        it.toString()
} ?: "MissingNo. ##"

fun List<PokemonTypesUiEntity>.formatPokemonType(
    placeholder: String
): String = this.fold(""){ acc, pokemonTypesUiEntity ->
    "$acc<font color=${pokemonTypesUiEntity.type.name.getTypeColorString()}>${pokemonTypesUiEntity.type.name.uppercase()}</font> "
}.ifEmpty { placeholder }

fun String.getTypeColorInt(): Int = when (this) {
    "normal" -> "#aab09f".toColorInt()
    "fighting" -> "#cb5f48".toColorInt()
    "flying" -> "#7da6de".toColorInt()
    "poison" -> "#b468b7".toColorInt()
    "ground" -> "#cc9f4f".toColorInt()
    "rock" -> "#b2a061".toColorInt()
    "bug" -> "#94bc4a".toColorInt()
    "ghost" -> "#846ab6".toColorInt()
    "steel" -> "#89a1b0".toColorInt()
    "fire" -> "#ea7a3c".toColorInt()
    "water" -> "#539ae2".toColorInt()
    "grass" -> "#71c558".toColorInt()
    "electric" -> "#e5c531".toColorInt()
    "psychic" -> "e5709b".toColorInt()
    "ice" -> "#70cbd4".toColorInt()
    "dragon" -> "#6a7baf".toColorInt()
    "dark" -> "#736c75".toColorInt()
    "fairy" -> "#e397d1".toColorInt()
    "stellar" -> "#7da6de".toColorInt()
    "unknown" -> "#81a596".toColorInt()
    "shadow" -> "#808080".toColorInt()
    else -> "#808080".toColorInt()
}

fun String.getTypeColorString(): String = when (this) {
    "normal" -> "#aab09f"
    "fighting" -> "#cb5f48"
    "flying" -> "#7da6de"
    "poison" -> "#b468b7"
    "ground" -> "#cc9f4f"
    "rock" -> "#b2a061"
    "bug" -> "#94bc4a"
    "ghost" -> "#846ab6"
    "steel" -> "#89a1b0"
    "fire" -> "#ea7a3c"
    "water" -> "#539ae2"
    "grass" -> "#71c558"
    "electric" -> "#e5c531"
    "psychic" -> "e5709b"
    "ice" -> "#70cbd4"
    "dragon" -> "#6a7baf"
    "dark" -> "#736c75"
    "fairy" -> "#e397d1"
    "stellar" -> "#7da6de"
    "unknown" -> "#81a596"
    "shadow" -> "#808080"
    else -> "#808080"
}