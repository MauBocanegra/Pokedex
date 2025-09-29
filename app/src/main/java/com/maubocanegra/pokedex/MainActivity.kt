package com.maubocanegra.pokedex

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.maubocanegra.pokedex.core.navigation.NavigationWrapper
import com.maubocanegra.pokedex.pokemonstaggered.presentation.PokemonStaggeredActivity
import com.maubocanegra.pokedex.ui.theme.PokedexTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*
        enableEdgeToEdge()
        supportActionBar?.hide()
        setContent {
            PokedexTheme {
                NavigationWrapper()
            }
        }
        */

        startActivity(Intent(this, PokemonStaggeredActivity::class.java))
    }
}
