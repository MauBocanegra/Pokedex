package com.maubocanegra.pokedex.pokemonrecyclerview.domain.usecase

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.maubocanegra.pokedex.pokemon.domain.usecase.GetPokemonImageAssetUseCase
import com.maubocanegra.pokedex.pokemonrecyclerview.domain.uistate.PokemonImageUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoadPokemonItemImageUseCase @Inject constructor(
    private val getPokemonImageAsset: GetPokemonImageAssetUseCase,
) {
    private val activeJobsByItemId = mutableMapOf<Int, Job>()
    private val _imageStateById = MutableStateFlow<Map<Int, PokemonImageUiState>>(emptyMap())
    val imageStateById: StateFlow<Map<Int, PokemonImageUiState>> = _imageStateById

    fun onItemBound(scope: CoroutineScope, itemId: Int, pngUrl: String) {
        val running = activeJobsByItemId[itemId]
        if (running?.isActive == true) return

        setState(itemId, PokemonImageUiState.Loading)

        activeJobsByItemId[itemId] = scope.launch {
            val result = getPokemonImageAsset(pngUrl)
            val nextState = result.getOrNull()
                ?.let { asset -> decodeFromPath(asset.absolutePath) }
                ?.let { bmp -> PokemonImageUiState.Ready(bmp) }
                ?: PokemonImageUiState.Error

            setState(itemId, nextState)
            activeJobsByItemId.remove(itemId)
        }
    }

    suspend fun decodeFromPath(path: String): Bitmap? = withContext(Dispatchers.Default) {
        val options = BitmapFactory.Options().apply { inPreferredConfig = Bitmap.Config.ARGB_8888 }
        BitmapFactory.decodeFile(path, options)
    }

    fun onItemRecycled(itemId: Int) {
        activeJobsByItemId.remove(itemId)?.cancel()
        //setState(itemId, PokemonImageUiState.Idle)
    }

    fun clearAll() {
        activeJobsByItemId.values.forEach { it.cancel() }
        activeJobsByItemId.clear()
        _imageStateById.value = emptyMap()
    }

    private fun setState(itemId: Int, state: PokemonImageUiState) {
        _imageStateById.value = _imageStateById.value.toMutableMap().apply { put(itemId, state) }
    }
}