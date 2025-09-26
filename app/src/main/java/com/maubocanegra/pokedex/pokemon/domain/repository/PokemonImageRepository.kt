package com.maubocanegra.pokedex.pokemon.domain.repository

interface PokemonImageRepository {
    suspend fun getImageFile(url: String): Result<ImageAsset>
    suspend fun prefetch(urls: List<String>)
    suspend fun evict(url: String)
}

data class ImageAsset(
    val absolutePath: String,   // plain String keeps domain Android-free
    val mimeType: String        // e.g., "image/png"
)