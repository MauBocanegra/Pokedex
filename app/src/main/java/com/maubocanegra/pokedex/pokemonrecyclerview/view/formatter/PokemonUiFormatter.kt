package com.maubocanegra.pokedex.pokemonrecyclerview.view.formatter

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonTypesUiEntity
import java.util.Locale
import androidx.core.graphics.toColorInt
import androidx.core.text.toSpannable

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

fun List<PokemonTypesUiEntity>.createSpannable(): Spannable{
    val spannableStringBuilder = SpannableStringBuilder()
    this.forEach { pokemonTypeUiEntity ->
        val type = pokemonTypeUiEntity.type.name.uppercase()
        spannableStringBuilder.append("$type ")
        println(type.lowercase())
        spannableStringBuilder.setSpan(
            ForegroundColorSpan(
                type.lowercase().parseTypeToIntColor()
            ),
            spannableStringBuilder.indexOf(type),
            spannableStringBuilder.indexOf(type)+type.length,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE,
        )
    }
    return spannableStringBuilder.toSpannable()
}

fun String.parseTypeToIntColor(): Int = this.getTypeColorString().toColorInt()

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
    "psychic" -> "#e5709b"
    "ice" -> "#70cbd4"
    "dragon" -> "#6a7baf"
    "dark" -> "#736c75"
    "fairy" -> "#e397d1"
    "stellar" -> "#7da6de"
    "unknown" -> "#81a596"
    "shadow" -> "#808080"
    else -> "#808080"
}