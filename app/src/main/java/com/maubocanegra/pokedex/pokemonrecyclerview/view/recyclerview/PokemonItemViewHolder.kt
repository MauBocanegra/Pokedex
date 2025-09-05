package com.maubocanegra.pokedex.pokemonrecyclerview.view.recyclerview

import androidx.recyclerview.widget.RecyclerView
import com.maubocanegra.pokedex.databinding.ItemPokemonBinding
import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonUiEntity
import java.util.Locale

class PokemonItemViewHolder(
    private  val binding: ItemPokemonBinding,
    private val onItemClicked: (name: String, url: String) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(pokemon: PokemonUiEntity) { with(binding){
        pokemonName.text = pokemon.name?.replaceFirstChar {
            if (it.isLowerCase())
                it.titlecase(Locale.getDefault())
            else
                it.toString()
        } ?: "MissingNo. ##"
        pokemonIndex.text = pokemon.id.toString().formatPokedexId()

        // placeholder for now
        pokemonImage.setImageResource(android.R.color.transparent)

        val name = pokemon.name
        val url = pokemon.url
        if(!name.isNullOrEmpty() && !url.isNullOrEmpty()){
            binding.root.setOnClickListener {
                onItemClicked(name, url)
            }
        }
    }}

    private fun String.formatPokedexId(): String = when(this.length){
        1 -> "#000$this"
        2 -> "#00$this"
        3 -> "#0$this"
        else -> "#$this"
    }
}