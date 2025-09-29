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
import com.maubocanegra.pokedex.pokemonrecyclerview.domain.uistate.PokemonDetailItemState
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
    private var previousDetailStateById: Map<Int, PokemonDetailItemState> = emptyMap()
    private val pendingPayloads = linkedSetOf<Pair<Int, PokemonPayload>>()
    private var drainPosted = false

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
            onImageRecycled = { id ->
                pokemonViewModel.onItemRecycledForImage(id)
            },
            imageStateForId = { id ->
                pokemonViewModel.uiState.value.imageStateById[id]
            },
            detailStateForId = { id ->
                pokemonViewModel.uiState.value.detailStateById[id]
            }
        )
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = pokemonAdapter
            setHasFixedSize(true) // optimization
            itemAnimator = null
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

                // diff image states
                state.imageStateById.forEach { (id, cur) ->
                    val prev = previousImageStateById[id]
                    if (prev != cur) enqueuePayload(id, PokemonPayload.ImageStateChanged)
                }
                previousImageStateById = state.imageStateById

                // diff detail states
                state.detailStateById.forEach { (id, cur) ->
                    val prev = previousDetailStateById[id]
                    if (prev != cur) enqueuePayload(id, PokemonPayload.DetailStateChanged)
                }
                previousDetailStateById = state.detailStateById

                drainPayloads(state.pokemonList)
            }
        }
    }

    private fun enqueuePayload(id: Int, payload: PokemonPayload) {
        pendingPayloads += id to payload
    }

    private fun drainPayloads(list: List<PokemonUiEntity>) {
        if (drainPosted) return
        drainPosted = true
        binding.recyclerView.post {
            drainPosted = false
            if (pendingPayloads.isEmpty()) return@post
            val last = pokemonAdapter.itemCount - 1
            pendingPayloads.forEach { (id, payload) ->
                val index = list.indexOfFirst { it.id == id }
                if (index in 0..last) pokemonAdapter.notifyItemChanged(index, payload)
            }
            pendingPayloads.clear()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
