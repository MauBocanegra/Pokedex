package com.maubocanegra.pokedex.pokemonrecyclerview.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.maubocanegra.pokedex.databinding.FragmentPokemonRecyclerViewBinding
import com.maubocanegra.pokedex.pokemonrecyclerview.view.recyclerview.PokemonRecyclerViewAdapter
import com.maubocanegra.pokedex.pokemonrecyclerview.view.recyclerview.PokemonRecyclerViewViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PokemonRecyclerViewFragment : Fragment() {

    // Lambda will be assigned after creation in order to have an empty factory
    var navigateToPokemonDetail: ((name: String, url: String) -> Unit)? = null
    var itemViewAttached: ((pokemonIdOrName: String) -> Unit)? = null
    var itemViewDetached: ((pokemonIdOrName: String) -> Unit)? = null

    private val pokemonViewModel: PokemonRecyclerViewViewModel by viewModels()
    private lateinit var pokemonAdapter: PokemonRecyclerViewAdapter
    private var _binding: FragmentPokemonRecyclerViewBinding? = null
    private val binding get() = _binding!!

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
            onItemAttached = { pokemonIdOrName ->
                pokemonViewModel.pokemonItemAttachesToScreen(pokemonIdOrName)
            },
            onItemDetached = { pokemonIdOrName ->
                pokemonViewModel.pokemonItemDetachesFromScreen(pokemonIdOrName)
            },
        )
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = pokemonAdapter
            setHasFixedSize(true) // optimization
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            pokemonViewModel.uiState.collectLatest { state ->
                pokemonAdapter.submitList(state.pokemonList)
                // Handle state.uiState (LOADING, FAILED) here
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
