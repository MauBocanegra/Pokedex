package com.maubocanegra.pokedex.pokemonrecyclerview.view.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.maubocanegra.pokedex.R
import com.maubocanegra.pokedex.databinding.ItemPokemonBinding
import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonUiEntity
import com.maubocanegra.pokedex.pokemonrecyclerview.domain.uistate.PokemonDetailItemState
import com.maubocanegra.pokedex.pokemonrecyclerview.domain.uistate.PokemonImageUiState
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

                    is PokemonPayload.ImageStateChanged -> {
                    }
                    is PokemonPayload.DetailStateChanged -> {

                    }

                }
            }
        }
    }

    fun bind(
        pokemon: PokemonUiEntity,
        state: PokemonImageUiState?
    ) { with(binding){
        pokemonName.text = pokemon.name?.formatPokemonName()
        pokemonIndex.text = pokemon.id.toString().formatPokedexId(
            binding.root.context.getString(R.string.placeholder_pokedex_id)
        )

        binding.pokemonType.text = pokemon.types?.createSpannable()

        // placeholder for now
        //pokemonImage.setImageResource(android.R.color.transparent)
        renderImage(state)

        val name = pokemon.name
        val url = pokemon.url
        if(!name.isNullOrEmpty() && !url.isNullOrEmpty()){
            binding.root.setOnClickListener {
                onItemClicked(name, url)
            }
        }
    }}

    /** Render-only. Adapter calls this when image state changes. */
    fun renderImage(state: PokemonImageUiState?) {
        val imageView = binding.pokemonImage
        when (state) {
            is PokemonImageUiState.Ready -> {
                binding.progressBar2.visibility = View.GONE
                imageView.setImageBitmap(state.bitmap)
            }
            is PokemonImageUiState.Error -> {
                binding.progressBar2.visibility = View.GONE
                imageView.setImageResource(R.drawable.pokeballart2)
            }
            is PokemonImageUiState.Loading -> {
                binding.progressBar2.visibility = View.VISIBLE
            }
            PokemonImageUiState.Idle, null -> {
                binding.progressBar2.visibility = View.GONE
                // Set placeholder only if there is no bitmap already shown
                // val hasBitmap = imageView.drawable is android.graphics.drawable.BitmapDrawable
                // if (!hasBitmap) imageView.setImageResource(R.drawable.pokeballart1)
            }
        }
    }

    fun renderDetail(state: PokemonDetailItemState?){
        when(state){
            PokemonDetailItemState.Loading -> {
                binding.progressBarType.visibility = View.VISIBLE
            }
            PokemonDetailItemState.Ready -> {
                binding.progressBarType.visibility = View.GONE
            }
            PokemonDetailItemState.Error -> {
                binding.progressBarType.visibility = View.GONE
            }
            null, PokemonDetailItemState.Idle -> {
                binding.progressBarType.visibility = View.GONE
            }
        }
    }

    /** Optional: call from onViewRecycled to avoid stale bitmaps. */
    fun clear() {
        binding.progressBar2.visibility = View.GONE
        binding.pokemonImage.setImageDrawable(null)
        binding.root.setOnClickListener(null)
    }
}
