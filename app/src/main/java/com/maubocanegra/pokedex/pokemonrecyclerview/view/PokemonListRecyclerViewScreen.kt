package com.maubocanegra.pokedex.pokemonrecyclerview.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import com.maubocanegra.pokedex.R
import com.maubocanegra.pokedex.pokemonrecyclerview.view.fragment.PokemonRecyclerViewFragment

@Composable
fun PokemonListRecyclerViewScreen(
    navigateToPokemonDetail: (name: String, url: String) -> Unit,
    itemViewAttached: ((pokemonId: Int) -> Unit)? = null,
    itemViewDetached: ((pokemonId: Int) -> Unit)? = null,
) {
    val context = LocalContext.current
    val fragmentActivity = context as? FragmentActivity
        ?: throw IllegalStateException("Context is not a FragmentActivity")
    val fragmentManager = fragmentActivity.supportFragmentManager

    // Preserve a single container view across recompositions
    val fragmentContainerView = remember {
        FragmentContainerView(context).apply {
            id = R.id.pokemon_list_fragment_container
        }
    }

    // Preserve the latest lambda reference
    val navigateUpdated by rememberUpdatedState(newValue = navigateToPokemonDetail)

    Scaffold { padding ->
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            factory = { fragmentContainerView }
        )

        DisposableEffect(fragmentManager) {
            val fragment = PokemonRecyclerViewFragment.newInstance().apply {
                this.navigateToPokemonDetail = navigateUpdated
                this.itemViewAttached = itemViewAttached
                this.itemViewDetached = itemViewDetached
            }
            fragmentManager.commit {
                replace(fragmentContainerView.id, fragment)
            }
            onDispose {}
        }
    }
}
