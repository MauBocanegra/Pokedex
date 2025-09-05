package com.maubocanegra.pokedex.pokemonrecyclerview.view.recyclerview

import androidx.recyclerview.widget.DiffUtil
import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonUiEntity

class PokemonListDiffCallback (
    private val oldList: List<PokemonUiEntity>,
    private val newList: List<PokemonUiEntity>,
): DiffUtil.Callback(){

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        return oldItem.name == newItem.name &&
                oldItem.url == newItem.url &&
                oldItem.baseExperience == newItem.baseExperience &&
                oldItem.height == newItem.height &&
                oldItem.order == newItem.order &&
                oldItem.weight == newItem.weight &&
                oldItem.abilities == newItem.abilities &&
                oldItem.forms == newItem.forms &&
                oldItem.locationAreaEncounters == newItem.locationAreaEncounters &&
                oldItem.moves == newItem.moves &&
                oldItem.sprites == newItem.sprites &&
                oldItem.stats == newItem.stats &&
                oldItem.types == newItem.types &&
                oldItem.officialArtwork == newItem.officialArtwork &&
                oldItem.criesLatest == newItem.criesLatest
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        val diff = mutableMapOf<String, Any?>()

        if (oldItem.name != newItem.name) diff["name"] = newItem.name
        if (oldItem.url != newItem.url) diff["url"] = newItem.url
        if (oldItem.baseExperience != newItem.baseExperience) diff["baseExperience"] = newItem.baseExperience
        if (oldItem.height != newItem.height) diff["height"] = newItem.height
        if (oldItem.order != newItem.order) diff["order"] = newItem.order
        if (oldItem.weight != newItem.weight) diff["weight"] = newItem.weight
        if (oldItem.abilities != newItem.abilities) diff["abilities"] = newItem.abilities
        if (oldItem.forms != newItem.forms) diff["forms"] = newItem.forms
        if (oldItem.locationAreaEncounters != newItem.locationAreaEncounters) diff["locationAreaEncounters"] = newItem.locationAreaEncounters
        if (oldItem.moves != newItem.moves) diff["moves"] = newItem.moves
        if (oldItem.sprites != newItem.sprites) diff["sprites"] = newItem.sprites
        if (oldItem.stats != newItem.stats) diff["stats"] = newItem.stats
        if (oldItem.types != newItem.types) diff["types"] = newItem.types
        if (oldItem.officialArtwork != newItem.officialArtwork) diff["officialArtwork"] = newItem.officialArtwork
        if (oldItem.criesLatest != newItem.criesLatest) diff["criesLatest"] = newItem.criesLatest

        return if (diff.isEmpty()) null else diff
    }
}
