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
        if (oldItemPosition !in oldList.indices || newItemPosition !in newList.indices) {
            return null
        }

        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        val diff = mutableMapOf<String, Any?>()

        if (oldItem.name != newItem.name) diff["name"] = newItem.name
        if (oldItem.url != newItem.url) diff["url"] = newItem.url

        var typeHasChanges = false
        oldItem.types?.zip(newItem.types.orEmpty())?.forEach { (oldType, newType) ->
            val slotHasChanged = oldType.slot != newType.slot
            val nameHasChanged = oldType.type.name != newType.type.name
            val urlHasChanged = oldType.type.url != newType.type.url
            if (slotHasChanged || nameHasChanged || urlHasChanged) {
                typeHasChanges = true
            }
        }
        if (typeHasChanges) {
            diff["types"] = newItem.types
        }

        return if (diff.isEmpty()) null else diff
    }
}
