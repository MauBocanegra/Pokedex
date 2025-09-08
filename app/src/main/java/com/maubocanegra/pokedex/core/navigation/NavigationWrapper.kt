package com.maubocanegra.pokedex.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.maubocanegra.pokedex.pokemondetail.view.PokemonDetailScreen
import com.maubocanegra.pokedex.pokemonlist.view.PokemonListScreen
import com.maubocanegra.pokedex.pokemonrecyclerview.view.PokemonListRecyclerViewScreen

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = PokemonRecyclerViewScreenNavigation,
    ) {
        composable<PokemonListScreenNavigation> {
            PokemonListScreen(
                navigateToPokemonDetail = { name, url ->
                    navController.navigate(
                        PokemonDetailScreenNavigation(name, url)
                    )
                }
            )
        }
        composable<PokemonDetailScreenNavigation> { navBackStackEntry ->
            val detail =
                navBackStackEntry.toRoute<PokemonDetailScreenNavigation>()
            PokemonDetailScreen(
                pokemonId = detail.pokemonUrl.trimEnd('/').split("/").lastOrNull()?.toInt() ?: 0,
                navigateBack = {
                    navController.popBackStack()
                },
            )
        }
        composable<PokemonRecyclerViewScreenNavigation> {
            PokemonListRecyclerViewScreen (
                navigateToPokemonDetail = { name, url ->
                    navController.navigate(
                        PokemonDetailScreenNavigation(name, url)
                    )
                }
            )
        }
    }
}
