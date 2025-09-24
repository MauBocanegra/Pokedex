package com.maubocanegra.pokedex.pokemonrecyclerview.view.recyclerview

import androidx.recyclerview.widget.RecyclerView
import com.maubocanegra.pokedex.R
import com.maubocanegra.pokedex.databinding.ItemPokemonBinding
import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonUiEntity
import com.maubocanegra.pokedex.pokemonrecyclerview.view.formatter.createSpannable
import com.maubocanegra.pokedex.pokemonrecyclerview.view.formatter.formatPokedexId
import com.maubocanegra.pokedex.pokemonrecyclerview.view.formatter.formatPokemonName
import com.maubocanegra.pokedex.pokemonrecyclerview.view.payloadmapper.PokemonPayload

/**
 * UI Formatters can be found at @see PokemonUiFormatter
 * */
class PokemonItemViewHolder(
    private  val binding: ItemPokemonBinding,
    private val onItemClicked: (name: String, url: String) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bindPartial(diff: List<PokemonPayload>){
        if(diff.isNotEmpty()){
            diff.forEach { pokemonPayload ->
                when(pokemonPayload){
                    is PokemonPayload.Name ->
                        binding.pokemonName.text =
                            pokemonPayload.name.formatPokemonName()
                    is PokemonPayload.Types -> {
                        binding.pokemonType.text = pokemonPayload.types.createSpannable()
                    }
                    is PokemonPayload.Url, is  PokemonPayload.Sprites -> {
                        // No visible changes yet
                    }
                }
            }
        }
    }

    fun bind(pokemon: PokemonUiEntity) { with(binding){
        pokemonName.text = pokemon.name?.formatPokemonName()
        pokemonIndex.text = pokemon.id.toString().formatPokedexId(
            binding.root.context.getString(R.string.placeholder_pokedex_id)
        )

        binding.pokemonType.text = pokemon.types?.createSpannable()

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
}
