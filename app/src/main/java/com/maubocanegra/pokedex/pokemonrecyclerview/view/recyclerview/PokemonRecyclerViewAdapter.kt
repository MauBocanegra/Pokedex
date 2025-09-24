package com.maubocanegra.pokedex.pokemonrecyclerview.view.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.maubocanegra.pokedex.databinding.ItemPokemonBinding
import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonUiEntity
import com.maubocanegra.pokedex.pokemonrecyclerview.view.payloadmapper.PokemonPayload

class PokemonRecyclerViewAdapter(
    private val onItemAttached: (Int) -> Unit,
    private val onItemDetached: (Int) -> Unit,
    private val onItemClicked: (name: String, url: String) -> Unit,
): RecyclerView.Adapter<PokemonItemViewHolder>() {

    private val items: MutableList<PokemonUiEntity> = mutableListOf()

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return items[position].id.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonItemViewHolder {
        val binding = ItemPokemonBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PokemonItemViewHolder(
            binding = binding,
            onItemClicked = onItemClicked,
        )
    }

    override fun onBindViewHolder(holder: PokemonItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun onBindViewHolder(
        holder: PokemonItemViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if(payloads.isEmpty()){
            super.onBindViewHolder(holder, position, payloads)
        } else {
            val typedPokemonPayload = payloads.filterIsInstance<PokemonPayload>()
            holder.bindPartial(typedPokemonPayload)
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onViewAttachedToWindow(holder: PokemonItemViewHolder) {
        super.onViewAttachedToWindow(holder)
        val position = holder.bindingAdapterPosition

        if(position != RecyclerView.NO_POSITION && !items[position].name.isNullOrEmpty()){
            onItemAttached(items[position].id)
        }
    }

    override fun onViewDetachedFromWindow(holder: PokemonItemViewHolder) {
        super.onViewDetachedFromWindow(holder)
        val position = holder.bindingAdapterPosition
        if(position != RecyclerView.NO_POSITION && !items[position].name.isNullOrEmpty()){
            onItemDetached(items[position].id)
        }
    }

    fun submitList(newList: List<PokemonUiEntity>) {
        val diffCallback = PokemonListDiffCallback(items, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        items.clear()
        items.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }
}
