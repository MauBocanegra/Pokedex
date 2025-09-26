package com.maubocanegra.pokedex.pokemon.domain.usecase

import android.util.Log
import com.maubocanegra.pokedex.pokemon.domain.repository.ImageAsset
import com.maubocanegra.pokedex.pokemon.domain.repository.PokemonImageRepository
import javax.inject.Inject

class GetPokemonImageAssetUseCase @Inject constructor(
    private val pokemonImageRepository: PokemonImageRepository
){
    suspend operator fun invoke(url: String): Result<ImageAsset>{
        Log.d("MauDebug", "url: ${url}")
        return pokemonImageRepository.getImageFile(url)
    }
}