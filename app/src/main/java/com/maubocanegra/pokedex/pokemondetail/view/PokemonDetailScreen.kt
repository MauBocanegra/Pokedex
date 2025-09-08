package com.maubocanegra.pokedex.pokemondetail.view


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maubocanegra.pokedex.R
import com.maubocanegra.pokedex.pokemondetail.domain.entity.PokemonUiEntity
import com.maubocanegra.pokedex.pokemonlist.domain.model.UIState
import java.util.Locale

@Composable
fun PokemonDetailScreen(
    pokemonId: Int,
    navigateBack: () -> Unit,
    pokemonDetailViewModel: PokemonDetailViewModel = hiltViewModel<PokemonDetailViewModel>()
) {

    LaunchedEffect(pokemonId) {
        pokemonDetailViewModel.getPokemonDetail(pokemonId)
    }

    val pokemonUiState = pokemonDetailViewModel
        .pokemonDetailState
        .collectAsStateWithLifecycle()
        .value

    Scaffold (
        topBar = { PokemonDetailTopAppBar(navigateBack) }
    ){ padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            when (pokemonUiState.uiState) {
                UIState.LOADING -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }
                UIState.SUCCESS, UIState.LOADING_NEXT_PAGE -> {
                    PokemonDetailView(pokemonUiState.pokemon)
                }
                //TODO Create another UI State
                UIState.FAILED -> {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = "Error: ${pokemonUiState.errorMessage}"
                    )
                }
            }
        }
    }

}

@Composable
fun PokemonDetailView(pokemon: PokemonUiEntity?){

    Column (
        modifier = Modifier.padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){

        Box (
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentAlignment = Alignment.Center
        ){
            Image(
                modifier = Modifier
                    .fillMaxHeight()
                    .alpha(0.2f),
                painter = painterResource(R.drawable.pokeballart1),
                contentScale = ContentScale.Fit,
                contentDescription = ""
            )

            Text(
                text = (pokemon?.name ?: "").replaceFirstChar {
                    if (it.isLowerCase())
                        it.titlecase(Locale.getDefault())
                    else
                        it.toString()
                },
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FancyCard("XP", "${pokemon?.baseExperience ?: 0}", R.drawable.pokeballart2)
            FancyCard("Height", "${pokemon?.height ?: 0}cm", R.drawable.pokeballart2)
            FancyCard("Weight", "${pokemon?.weight ?: 0}kg", R.drawable.pokeballart2)
        }


        Spacer(Modifier.height(16.dp))

        val typesText = pokemon?.types?.joinToString(separator = "\n") { eachPokemon ->
            eachPokemon.type.name
        }

        ListItem(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            headlineContent = {
                Text(
                    text = "Types",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            supportingContent = {
                Text(typesText ?: "")
            },
            shadowElevation = 4.dp
        )

        Spacer(Modifier.height(16.dp))

        val statsText = pokemon?.stats?.joinToString(separator = "\n") { eachPokemon ->
            "${eachPokemon.stat.name}: ${eachPokemon.baseStat}"
        }

        ListItem(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            headlineContent = {
                Text(
                    text = "Stats",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            supportingContent = {
                Text(statsText ?: "")
            },
            shadowElevation = 4.dp
        )

        Spacer(Modifier.height(16.dp))

        val abilitiesText = pokemon?.abilities?.joinToString(separator = "\n") { eachPokemon ->
            "${eachPokemon.ability.name} isHidden: ${eachPokemon.isHidden}"
        }

        ListItem(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            headlineContent = {
                Text(
                    text = "Abilities",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            supportingContent = {
                Text(abilitiesText ?: "")
            },
            shadowElevation = 4.dp
        )

        Spacer(Modifier.height(16.dp))

        val movesText = pokemon?.moves?.joinToString(separator = "\n") { eachPokemon ->
            "Name: ${eachPokemon.move.name}"
        }

        ListItem(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            headlineContent = {
                Text(
                    text = "Moves",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            supportingContent = {
                Text(movesText ?: "")
            },
            shadowElevation = 4.dp
        )

        Spacer(Modifier.height(36.dp))

        Text(
            text = pokemon.toString(),
            fontSize = 10.sp,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailTopAppBar(
    navigateBack: () -> Unit,
){
    TopAppBar(
        title = { Text(
            text = "Pokemon Detail",
            style = MaterialTheme.typography.titleLarge
        )},
        navigationIcon = {
            IconButton (onClick = navigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
    )
}

@Composable
fun FancyCard(
    title: String,
    subtitle: String,
    backgroundImage: Int // drawable resource
) {
    Card(
        modifier = Modifier.wrapContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent, //Card content color,e.g.text
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier.width(120.dp).height(120.dp)// 👈 fixed size, or you can make it adaptive
        ) {
            // Background Image
            Image(
                painter = painterResource(id = backgroundImage),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.15f
            )

            // Foreground content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
