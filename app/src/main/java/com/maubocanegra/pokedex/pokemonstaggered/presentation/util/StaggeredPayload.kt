package com.maubocanegra.pokedex.pokemonstaggered.presentation.util

sealed interface StaggeredPayload {
    data object ImageChanged : StaggeredPayload
    data object DetailChanged : StaggeredPayload
}