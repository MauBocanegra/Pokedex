package com.maubocanegra.pokedex.pokemonstaggered.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.maubocanegra.pokedex.databinding.FragmentPokemonStaggeredBinding
import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonUiEntity
import com.maubocanegra.pokedex.pokemonrecyclerview.domain.uistate.PokemonDetailItemState
import com.maubocanegra.pokedex.pokemonrecyclerview.domain.uistate.PokemonImageUiState
import com.maubocanegra.pokedex.pokemonstaggered.presentation.item.StaggeredItemCardShape
import com.maubocanegra.pokedex.pokemonstaggered.presentation.item.StaggeredPokemonViewHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PokemonStaggeredFragment : Fragment() {

    companion object { fun newInstance() = PokemonStaggeredFragment() }

    private var _binding: FragmentPokemonStaggeredBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PokemonStaggeredViewModel by viewModels()
    private lateinit var adapter: StaggeredPokemonAdapter

    private var lastImages: Map<Int, PokemonImageUiState> = emptyMap()
    private var lastDetails: Map<Int, PokemonDetailItemState> = emptyMap()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPokemonStaggeredBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observePaging()
        observePerItemStates()
    }

    private fun setupRecyclerView() {
        adapter = StaggeredPokemonAdapter(
            imageStateForId = { id -> viewModel.imageStateFor(id) },
            detailStateForId = { id -> viewModel.detailStateFor(id) },
            onItemAttached = { id -> viewModel.onItemAttached(id) },
            onItemDetached = { id -> viewModel.onItemDetached(id) },
            onItemRecycled = { id -> viewModel.onItemRecycled(id) },
            onItemClicked = { name, url -> viewModel.onItemClicked(name, url) },
            itemShapeFor = ::shapeFor
        ).apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        val gridLayoutManager = StaggeredGridLayoutManager(
            2,
            StaggeredGridLayoutManager.VERTICAL
        ).apply {
            gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
            isItemPrefetchEnabled = false
        }

        binding.recyclerView.apply {
            layoutManager = gridLayoutManager
            adapter = this@PokemonStaggeredFragment.adapter
            setHasFixedSize(true)
            setItemViewCacheSize(10)
            (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        }

        adapter.addLoadStateListener { states ->
            if (states.append.endOfPaginationReached || states.append is LoadState.NotLoading) {
                binding.recyclerView.post {
                    (binding.recyclerView.layoutManager as? StaggeredGridLayoutManager)
                        ?.invalidateSpanAssignments()
                }
            }
        }
    }

    private fun observePaging() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.pagingData.collectLatest { data ->
                adapter.submitData(viewLifecycleOwner.lifecycle, data)
            }
        }
    }

    private fun observePerItemStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.imageStateMap.collectLatest { current ->
                val changed = current.keys.filter { id -> current[id] != lastImages[id] }.toSet()
                lastImages = current
                applyVisibleImageUpdates(changed)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.detailStateMap.collectLatest { current ->
                val changed = current.keys.filter { id -> current[id] != lastDetails[id] }.toSet()
                lastDetails = current
                applyVisibleDetailUpdates(changed)
            }
        }
    }

    private fun applyVisibleImageUpdates(changedIds: Set<Int>) {
        if (changedIds.isEmpty()) return
        val recyclerView = binding.recyclerView
        recyclerView.post {
            for (i in 0 until recyclerView.childCount) {
                val viewHolder = recyclerView.getChildViewHolder(
                    recyclerView.getChildAt(i)
                ) as? StaggeredPokemonViewHolder ?: continue
                val id = viewHolder.boundItemId() ?: continue
                if (id in changedIds) viewHolder.renderImage(viewModel.imageStateFor(id))
            }
        }
    }

    private fun applyVisibleDetailUpdates(changedIds: Set<Int>) {
        if (changedIds.isEmpty()) return
        val recyclerView = binding.recyclerView
        recyclerView.post {
            for (i in 0 until recyclerView.childCount) {
                val viewHolder = recyclerView.getChildViewHolder(
                    recyclerView.getChildAt(i)
                ) as? StaggeredPokemonViewHolder ?: continue
                val id = viewHolder.boundItemId() ?: continue
                if (id in changedIds) viewHolder.renderDetail(viewModel.detailStateFor(id))
            }
        }
    }

    private fun shapeFor(item: PokemonUiEntity): StaggeredItemCardShape {
        return if (item.id % 3 == 0) StaggeredItemCardShape.TALL else StaggeredItemCardShape.SQUARE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}