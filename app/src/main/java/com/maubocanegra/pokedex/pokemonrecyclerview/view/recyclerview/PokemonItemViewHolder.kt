package com.maubocanegra.pokedex.pokemonrecyclerview.view.recyclerview

import android.text.Html
import android.text.Html.FROM_HTML_MODE_LEGACY
import androidx.recyclerview.widget.RecyclerView
import com.maubocanegra.pokedex.R
import com.maubocanegra.pokedex.databinding.ItemPokemonBinding
import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonTypesUiEntity
import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonUiEntity
import com.maubocanegra.pokedex.pokemonrecyclerview.view.formatter.formatPokedexId
import com.maubocanegra.pokedex.pokemonrecyclerview.view.formatter.formatPokemonName
import com.maubocanegra.pokedex.pokemonrecyclerview.view.formatter.formatPokemonType

/**
 * UI Formatters can be found at @see PokemonUiFormatter
 * */
class PokemonItemViewHolder(
    private  val binding: ItemPokemonBinding,
    private val onItemClicked: (name: String, url: String) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bindPartial(diff: Map<String, Any?>){
        diff.forEach { (key, value) ->
            when(key){
                "name" -> binding.pokemonName.text = (value as? String).formatPokemonName()
                "types" -> {
                    binding.pokemonType.text = Html.fromHtml(
                        (value as List<PokemonTypesUiEntity>).formatPokemonType(
                            binding.root.context.getString(R.string.placeholder_pokemon_type)
                        ),
                        FROM_HTML_MODE_LEGACY
                    )
                }
            }
        }
    }

    fun bind(pokemon: PokemonUiEntity) { with(binding){
        pokemonName.text = pokemon.name?.formatPokemonName()
        pokemonIndex.text = pokemon.id.toString().formatPokedexId(
            binding.root.context.getString(R.string.placeholder_pokedex_id)
        )

        pokemonType.text = Html.fromHtml(
            pokemon.types?.formatPokemonType(
                binding.root.context.getString(R.string.placeholder_pokemon_type)
            ),
            FROM_HTML_MODE_LEGACY
        )

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
