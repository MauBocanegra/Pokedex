package com.maubocanegra.pokedex.pokemonstaggered.presentation.item

enum class StaggeredItemCardShape(val viewType: Int) {
    SQUARE(1),
    TALL(2);

    companion object {
        fun from(viewType: Int): StaggeredItemCardShape =
            entries.firstOrNull { it.viewType == viewType } ?: TALL
    }
}