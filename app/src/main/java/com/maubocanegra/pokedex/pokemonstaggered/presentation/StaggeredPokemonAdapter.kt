package com.maubocanegra.pokedex.pokemonstaggered.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.maubocanegra.pokedex.databinding.ItemPokemonStaggeredSquareBinding
import com.maubocanegra.pokedex.databinding.ItemPokemonStaggeredTallBinding
import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonUiEntity
import com.maubocanegra.pokedex.pokemonrecyclerview.domain.uistate.PokemonDetailItemState
import com.maubocanegra.pokedex.pokemonrecyclerview.domain.uistate.PokemonImageUiState
import com.maubocanegra.pokedex.pokemonstaggered.presentation.item.StaggeredItemCardShape
import com.maubocanegra.pokedex.pokemonstaggered.presentation.item.StaggeredPokemonViewHolder
import com.maubocanegra.pokedex.pokemonstaggered.presentation.util.StaggeredPayload


class StaggeredPokemonAdapter(
    private val imageStateForId: (Int) -> PokemonImageUiState?,
    private val detailStateForId: (Int) -> PokemonDetailItemState?,
    private val onItemAttached: (Int) -> Unit,
    private val onItemDetached: (Int) -> Unit,
    private val onItemRecycled: (Int) -> Unit,
    private val onItemClicked: (name: String, url: String) -> Unit,
    private val itemShapeFor: (PokemonUiEntity) -> StaggeredItemCardShape
): PagingDataAdapter<PokemonUiEntity, StaggeredPokemonViewHolder>(Diff) {

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position) ?: return StaggeredItemCardShape.TALL.viewType
        return itemShapeFor(item).viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaggeredPokemonViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (StaggeredItemCardShape.from(viewType)) {
            StaggeredItemCardShape.TALL -> {
                val b = ItemPokemonStaggeredTallBinding.inflate(inflater, parent, false)
                StaggeredPokemonViewHolder.fromTall(b, onItemClicked)
            }
            StaggeredItemCardShape.SQUARE -> {
                val b = ItemPokemonStaggeredSquareBinding.inflate(inflater, parent, false)
                StaggeredPokemonViewHolder.fromSquare(b, onItemClicked)
            }
        }
    }

    override fun onBindViewHolder(holder: StaggeredPokemonViewHolder, position: Int) {
        val item = getItem(position) ?: return
        holder.bind(
            pokemon = item,
            imageState = imageStateForId(item.id),
            detailState = detailStateForId(item.id)
        )
    }

    override fun onBindViewHolder(
        holder: StaggeredPokemonViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
            return
        }
        val item = getItem(position) ?: return
        if (payloads.any { it is StaggeredPayload.ImageChanged }) {
            holder.renderImage(imageStateForId(item.id))
        }
        if (payloads.any { it is StaggeredPayload.DetailChanged }) {
            holder.renderDetail(detailStateForId(item.id))
        }
    }

    override fun onViewAttachedToWindow(holder: StaggeredPokemonViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.boundItemId()?.let(onItemAttached)
    }

    override fun onViewDetachedFromWindow(holder: StaggeredPokemonViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.boundItemId()?.let(onItemDetached)
    }

    override fun onViewRecycled(holder: StaggeredPokemonViewHolder) {
        holder.boundItemId()?.let(onItemRecycled)
        holder.clear()
        super.onViewRecycled(holder)
    }

    private object Diff : DiffUtil.ItemCallback<PokemonUiEntity>() {
        override fun areItemsTheSame(oldItem: PokemonUiEntity, newItem: PokemonUiEntity): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: PokemonUiEntity,
            newItem: PokemonUiEntity
        ): Boolean {
            val itemNamesAreEquals = oldItem.name == newItem.name
            val urlsAreEquals = oldItem.url == newItem.url

            var typesAreEquals = true
            oldItem.types?.zip(newItem.types.orEmpty())?.forEach { (oldType, newType) ->
                val slotIsTheSame = oldType.slot == newType.slot
                val nameIsTheSame = oldType.type.name == newType.type.name
                val urlIsTheSame = oldType.type.url == newType.type.url
                typesAreEquals = typesAreEquals && slotIsTheSame && nameIsTheSame && urlIsTheSame
            }

            return itemNamesAreEquals && urlsAreEquals && typesAreEquals
        }
    }

}