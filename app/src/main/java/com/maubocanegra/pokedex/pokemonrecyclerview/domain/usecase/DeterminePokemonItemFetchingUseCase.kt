package com.maubocanegra.pokedex.pokemonrecyclerview.domain.usecase

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

    private val millisVisibleBeforeRequestingDetail = 100L
    private val _pokemonItemAttachmentState = MutableSharedFlow<FetchPokemonSignal>()
    val pokemonAttachmentState: SharedFlow<FetchPokemonSignal> = _pokemonItemAttachmentState

    private val visibilityJobs = mutableMapOf<Int, Job>()
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
                            windowAttachmentState.pokemonId
                        )
                    )
                }
                visibilityJobs[windowAttachmentState.pokemonId] = job
            }

            is DetachedFromWindowState -> {
                visibilityJobs[windowAttachmentState.pokemonId]?.cancel()
                visibilityJobs.remove(windowAttachmentState.pokemonId)
            }
        }
    }

    sealed interface WindowAttachmentState
    data class AttachedToWindowState(
        val pokemonId: Int
    ): WindowAttachmentState
    data class DetachedFromWindowState(
        val pokemonId: Int
    ): WindowAttachmentState
    data class FetchPokemonSignal(val pokemonId: Int)
}
