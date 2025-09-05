package com.maubocanegra.pokedex.pokemonrecyclerview.domain.usecase

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class DeterminePokemonItemFetchingUseCase @Inject constructor(
) {

    private val millisVisibleBeforeRequestingDetail = 500L
    private val _pokemonItemAttachmentState = MutableSharedFlow<FetchPokemonSignal>()
    val pokemonAttachmentState: SharedFlow<FetchPokemonSignal> = _pokemonItemAttachmentState

    private val visibilityJobs = mutableMapOf<String, Job>()
    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.Default
    )

    fun handleAttachmentState(
        windowAttachmentState: WindowAttachmentState
    ){

        when(windowAttachmentState){
            is AttachedToWindowState -> {
                val job = scope.launch {
                    delay(millisVisibleBeforeRequestingDetail)
                    _pokemonItemAttachmentState.emit(
                        FetchPokemonSignal(
                            windowAttachmentState.pokemonIdOrName
                        )
                    )
                }
                visibilityJobs[windowAttachmentState.pokemonIdOrName] = job
            }

            is DetachedFromWindowState -> {
                visibilityJobs[windowAttachmentState.pokemonIdOrName]?.cancel()
                visibilityJobs.remove(windowAttachmentState.pokemonIdOrName)
            }
        }
    }

    sealed interface WindowAttachmentState
    data class AttachedToWindowState(
        val pokemonIdOrName: String
    ): WindowAttachmentState
    data class DetachedFromWindowState(
        val pokemonIdOrName: String
    ): WindowAttachmentState
    data class FetchPokemonSignal(val pokemonIdOrName: String)
}
