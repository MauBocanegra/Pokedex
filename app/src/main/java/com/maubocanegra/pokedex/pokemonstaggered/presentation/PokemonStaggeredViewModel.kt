package com.maubocanegra.pokedex.pokemonstaggered.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.maubocanegra.pokedex.pokemon.domain.usecase.GetPokemonDetailUseCase
import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonUiEntity
import com.maubocanegra.pokedex.pokemonrecyclerview.domain.uistate.PokemonDetailItemState
import com.maubocanegra.pokedex.pokemonrecyclerview.domain.uistate.PokemonImageUiState
import com.maubocanegra.pokedex.pokemonrecyclerview.domain.usecase.LoadPokemonItemImageUseCase
import com.maubocanegra.pokedex.pokemonstaggered.domain.GetStaggeredPokemonPagingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonStaggeredViewModel @Inject constructor(
    getStaggeredPokemonPaging: GetStaggeredPokemonPagingUseCase,
    private val getPokemonDetail: GetPokemonDetailUseCase,
    private val loadPokemonItemImage: LoadPokemonItemImageUseCase
): ViewModel() {


    val pagingData: StateFlow<PagingData<PokemonUiEntity>> =
        getStaggeredPokemonPaging()
            .cachedIn(viewModelScope)
            .let { upstream ->
                // expose as StateFlow for simple collection in Fragment
                MutableStateFlow<PagingData<PokemonUiEntity>>(PagingData.empty()).also { state ->
                    viewModelScope.launch {
                        upstream.collect { state.value = it }
                    }
                }
            }

    val imageStateMap: StateFlow<Map<Int, PokemonImageUiState>> =
        loadPokemonItemImage.imageStateById

    private val _detailStateMap = MutableStateFlow<Map<Int, PokemonDetailItemState>>(emptyMap())
    val detailStateMap: StateFlow<Map<Int, PokemonDetailItemState>> = _detailStateMap

    private val detailCoordinatorById = mutableMapOf<Int, Job>()

    fun onItemAttached(pokemonId: Int){
        if (detailCoordinatorById[pokemonId]?.isActive == true) return

        _detailStateMap.update { it + (pokemonId to PokemonDetailItemState.Loading) }

        detailCoordinatorById[pokemonId] = viewModelScope.launch {
            // Share the detail stream so UI state and URL-gating do not refetch
            val sharedDetail = getPokemonDetail(pokemonId)
                .shareIn(this, started = SharingStarted.Eagerly, replay = 1)

            // Mark Ready when the first Pokemon detail arrives (Room will update the list)
            sharedDetail
                .map { it.pokemon }
                .filterNotNull()
                .onEach { pokemonDetail ->
                    _detailStateMap.update { map ->
                        map + (pokemonId to PokemonDetailItemState.Ready(pokemonDetail))
                    }
                }
                .catch {
                    _detailStateMap.update { map -> map + (pokemonId to PokemonDetailItemState.Error) }
                }
                .launchIn(this)

            // Gate image loading on the first non-blank Home/frontDefault URL
            sharedDetail
                .map { d ->
                    d.pokemon?.sprites?.other?.homeSprites?.homeFrontDefault?.takeIf { it.isNotBlank() }
                        ?: d.pokemon?.sprites?.frontDefault?.takeIf { it.isNotBlank() }
                }
                .filterNotNull()
                .distinctUntilChanged()
                .take(1)
                .onEach { imageUrl ->
                    loadPokemonItemImage.onItemBound(
                        scope = viewModelScope,
                        itemId = pokemonId,
                        pngUrl = imageUrl
                    )
                }
                .catch { /* swallow errors for image gating */ }
                .launchIn(this)
        }
    }

    fun onItemDetached(pokemonId: Int) {
        detailCoordinatorById.remove(pokemonId)?.cancel()
        // Cancel any in-flight decode/fetch; do not reset Ready
        loadPokemonItemImage.onItemRecycled(pokemonId)
    }

    fun onItemRecycled(pokemonId: Int) {
        onItemDetached(pokemonId)
    }

    fun imageStateFor(id: Int): PokemonImageUiState? = imageStateMap.value[id]
    fun detailStateFor(id: Int): PokemonDetailItemState? = detailStateMap.value[id]

    fun onItemClicked(name: String, url: String) {
        // Hook to navigation as needed.
    }

    override fun onCleared() {
        detailCoordinatorById.values.forEach { it.cancel() }
        detailCoordinatorById.clear()
        super.onCleared()
    }
}