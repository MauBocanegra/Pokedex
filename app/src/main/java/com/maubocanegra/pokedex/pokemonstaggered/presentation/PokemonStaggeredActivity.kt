package com.maubocanegra.pokedex.pokemonstaggered.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.maubocanegra.pokedex.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PokemonStaggeredActivity : AppCompatActivity() {
    companion object {
        fun newIntent(context: Context): Intent =
            Intent(context, PokemonStaggeredActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokemon_staggered)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.staggered_container, PokemonStaggeredFragment.newInstance())
                .commit()
        }
    }
}