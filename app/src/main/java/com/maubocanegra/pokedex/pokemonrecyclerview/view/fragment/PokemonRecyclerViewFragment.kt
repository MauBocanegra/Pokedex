package com.maubocanegra.pokedex.pokemonrecyclerview.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maubocanegra.pokedex.databinding.FragmentPokemonRecyclerViewBinding
import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonUiEntity
import com.maubocanegra.pokedex.pokemonrecyclerview.domain.uistate.PokemonImageUiState
import com.maubocanegra.pokedex.pokemonrecyclerview.view.payloadmapper.PokemonPayload
import com.maubocanegra.pokedex.pokemonrecyclerview.view.recyclerview.PokemonRecyclerViewAdapter
import com.maubocanegra.pokedex.pokemonrecyclerview.view.recyclerview.PokemonRecyclerViewViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PokemonRecyclerViewFragment : Fragment() {

    // Lambda will be assigned after creation in order to have an empty factory
    var navigateToPokemonDetail: ((name: String, url: String) -> Unit)? = null
    var itemViewAttached: ((pokemonId: Int) -> Unit)? = null
    var itemViewDetached: ((pokemonId: Int) -> Unit)? = null

    private val pokemonViewModel: PokemonRecyclerViewViewModel by viewModels()
    private lateinit var pokemonAdapter: PokemonRecyclerViewAdapter
    private var _binding: FragmentPokemonRecyclerViewBinding? = null
    private val binding get() = _binding!!

    private var previousImageStateById: Map<Int, PokemonImageUiState> = emptyMap()
    private val pendingImageUpdates = linkedSetOf<Int>()
    private var payloadDrainPosted = false

    companion object {
        fun newInstance(): PokemonRecyclerViewFragment {
            return PokemonRecyclerViewFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPokemonRecyclerViewBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeUiState()
    }

    private fun setupRecyclerView() {
        pokemonAdapter = PokemonRecyclerViewAdapter(
            onItemClicked = { pokemonName, pokemonUrl ->
                navigateToPokemonDetail?.invoke(pokemonName, pokemonUrl)
            },
            onItemAttached = { pokemonId ->
                pokemonViewModel.pokemonItemAttachesToScreen(pokemonId)
            },
            onItemDetached = { pokemonId ->
                pokemonViewModel.pokemonItemDetachesFromScreen(pokemonId)
            },
            onImageBindRequested = { id, url ->
                pokemonViewModel.onItemBoundForImage(id, url)
           },
            onImageRecycled = { id ->
                pokemonViewModel.onItemRecycledForImage(id)
            },
            frontDefaultUrlFor = ::extractHomeFrontDefault,
            imageStateForId = {
                id -> pokemonViewModel.uiState.value.imageStateById[id]
            }
        )
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = pokemonAdapter
            setHasFixedSize(true) // optimization
        }

        binding.recyclerView.addOnScrollListener( object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy <= 0) return // only trigger on scroll down

                val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return

                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                val threshold = 15 // how close to the end to trigger next page

                if ((visibleItemCount + firstVisibleItemPosition) >= (totalItemCount - threshold)
                    && firstVisibleItemPosition >= 0
                ) {
                    // Tell the ViewModel to load next page
                    pokemonViewModel.loadNextPage()
                }
            }
        })
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            pokemonViewModel.uiState.collectLatest { state ->
                pokemonAdapter.submitList(state.pokemonList)
                //pokemonAdapter.appendList(state.pokemonList)
                // Handle state.uiState (LOADING, FAILED) here

                // image payload updates
                val current = state.imageStateById
                current.forEach { (id, newState) ->
                    val old = previousImageStateById[id]
                    if (old != newState) {
                        val index = state.pokemonList.indexOfFirst { it.id == id }
                        if (index != -1) {
                            enqueueImagePayload(index)
                        }
                    }
                }
                previousImageStateById = current
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // HomeSprite.frontDefault extractor
    private fun extractHomeFrontDefault(pokemon: PokemonUiEntity): String {
        return pokemon.sprites?.other?.homeSprites?.homeFrontDefault.takeIf {
            !it.isNullOrBlank() } ?: ""
    }

    // Defer + batch to avoid "computing layout" exceptions
    private fun enqueueImagePayload(index: Int) {
        pendingImageUpdates += index
        if (payloadDrainPosted) return
        payloadDrainPosted = true
        binding.recyclerView.post {
            payloadDrainPosted = false
            if (pendingImageUpdates.isEmpty()) return@post
            val toUpdate = pendingImageUpdates.toList()
            pendingImageUpdates.clear()
            val lastIndex = pokemonAdapter.itemCount - 1
            toUpdate.forEach { i ->
                if (i in 0..lastIndex) {
                    // Reuse existing payload type as a signal
                    pokemonAdapter.notifyItemChanged(i, PokemonPayload.Sprites(null))
                }
            }
        }
    }
}
