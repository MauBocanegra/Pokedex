package com.maubocanegra.pokedex.pokemonstaggered.presentation.item

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.maubocanegra.pokedex.R
import com.maubocanegra.pokedex.databinding.ItemPokemonStaggeredSquareBinding
import com.maubocanegra.pokedex.databinding.ItemPokemonStaggeredTallBinding
import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonUiEntity
import com.maubocanegra.pokedex.pokemonrecyclerview.domain.uistate.PokemonDetailItemState
import com.maubocanegra.pokedex.pokemonrecyclerview.domain.uistate.PokemonImageUiState
import com.maubocanegra.pokedex.pokemonrecyclerview.view.formatter.createSpannable
import com.maubocanegra.pokedex.pokemonrecyclerview.view.formatter.formatPokedexId
import com.maubocanegra.pokedex.pokemonrecyclerview.view.formatter.formatPokemonName

class StaggeredPokemonViewHolder(
    private val views: StaggeredPokemonViews,
    private val onItemClicked: (name: String, url: String) -> Unit
): RecyclerView.ViewHolder(views.root) {

    interface StaggeredPokemonViews {
        val root: View
        val name: TextView
        val index: TextView
        val type: TextView
        val image: ImageView
        val typeProgress: ProgressBar
    }

    companion object {
        fun fromSquare(
            binding: ItemPokemonStaggeredSquareBinding,
            onItemClicked: (String, String) -> Unit
        ) = StaggeredPokemonViewHolder(
            object : StaggeredPokemonViews {
                override val root = binding.root
                override val name = binding.pokemonName
                override val index = binding.pokemonIndex
                override val type = binding.pokemonType
                override val image = binding.pokemonImage
                override val typeProgress = binding.progressBarType
            },
            onItemClicked
        )

        fun fromTall(
            binding: ItemPokemonStaggeredTallBinding,
            onItemClicked: (String, String) -> Unit
        ) = StaggeredPokemonViewHolder(
            object : StaggeredPokemonViews  {
                override val root = binding.root
                override val name = binding.pokemonName
                override val index = binding.pokemonIndex
                override val type = binding.pokemonType
                override val image = binding.pokemonImage
                override val typeProgress = binding.progressBarType
            },
            onItemClicked
        )
    }

    private var boundId: Int? = null
    fun boundItemId(): Int? = boundId

    fun bind(
        pokemon: PokemonUiEntity,
        imageState: PokemonImageUiState?,
        detailState: PokemonDetailItemState?
    ) {
        boundId = pokemon.id

        views.name.text = pokemon.name?.formatPokemonName()
        views.index.text = pokemon.id.toString()
            .formatPokedexId(views.root.context.getString(R.string.placeholder_pokedex_id))
        views.type.text = pokemon.types?.createSpannable()

        renderImage(imageState)
        renderDetail(detailState)

        val name = pokemon.name
        val url = pokemon.url
        views.root.setOnClickListener(
            if (!name.isNullOrEmpty() && !url.isNullOrEmpty()) { { onItemClicked(name, url) } }
            else null
        )
    }

    fun renderImage(state: PokemonImageUiState?) {
        when (state) {
            is PokemonImageUiState.Ready -> views.image.setImageBitmap(state.bitmap)
            is PokemonImageUiState.Error -> views.image.setImageResource(R.drawable.pokeballart2)
            PokemonImageUiState.Loading,
            PokemonImageUiState.Idle,
            null -> {
                //val hasBitmap = views.image.drawable is BitmapDrawable
                //if (!hasBitmap) views.image.setImageResource(R.drawable.pokeballart1)
            }
        }
    }

    fun renderDetail(state: PokemonDetailItemState?) {
        when (state) {
            PokemonDetailItemState.Loading -> {
                //views.type.alpha = 0.5f
                //views.typeProgress.visibility = View.VISIBLE
            }
            is PokemonDetailItemState.Ready -> {
                views.type.text = state.pokemonDetail.types?.createSpannable()
            }
            PokemonDetailItemState.Error,
            PokemonDetailItemState.Idle,
            null -> {
                //views.type.alpha = 1f
                //views.typeProgress.visibility = View.GONE
            }
        }
    }

    fun clear() {
        boundId = null
        views.image.setImageDrawable(null)
        views.type.text = ""
        //views.type.alpha = 1f
        //views.typeProgress.visibility = View.GONE
        //views.root.setOnClickListener(null)
    }
}