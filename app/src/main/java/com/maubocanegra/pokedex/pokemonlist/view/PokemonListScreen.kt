package com.maubocanegra.pokedex.pokemonlist.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.maubocanegra.pokedex.R
import com.maubocanegra.pokedex.pokemonlist.domain.model.PokemonListItemModel
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonListScreen(
    viewModel: PokemonListScreenViewModel = hiltViewModel(),
    navigateToPokemonDetail: (name: String, url: String) -> Unit,
) {
    val lazyPagingItems = viewModel.pokemonPagerState.collectAsLazyPagingItems()
    val listState = rememberLazyListState()
    val pullState = rememberPullToRefreshState()

    Scaffold (
        topBar = { PokemonListTopAppBar() }
    ) { padding ->
        PullToRefreshBox(
            state = pullState,
            onRefresh = { lazyPagingItems.refresh() },
            isRefreshing = lazyPagingItems.loadState.refresh is LoadState.Loading
        ) {
            when (val refreshState = lazyPagingItems.loadState.refresh) {
                is LoadState.Loading -> {
                    if (lazyPagingItems.itemCount == 0) {
                        // Initial loading
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator() }
                    } else {
                        // Paging has items but refresh is loading (pull-to-refresh)
                        PokemonListContent(lazyPagingItems, listState, Modifier.padding(padding), navigateToPokemonDetail)
                    }
                }

                is LoadState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = refreshState.error.localizedMessage ?: "Something went wrong")
                    }
                }

                else -> {
                    // LoadState.NotLoading -> show items
                    PokemonListContent(
                        lazyPagingItems,
                        listState,
                        Modifier.padding(padding),
                        navigateToPokemonDetail
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonListTopAppBar(){
    TopAppBar(
        title = { Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Choose your Pokemon",
            textAlign = TextAlign.Center,
        )},
    )
}

@Composable
private fun PokemonListContent(
    items: androidx.paging.compose.LazyPagingItems<PokemonListItemModel>,
    listState: androidx.compose.foundation.lazy.LazyListState,
    modifier: Modifier = Modifier,
    navigateToPokemonDetail: (name: String, url: String) -> Unit,
) {
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            count = items.itemCount,
            key = items.itemKey { it.name }
        ) { index ->
            val pokemon = items[index]
            if (pokemon != null) {
                PokemonRow(index, pokemon, navigateToPokemonDetail)
            }
        }

        // Infinite scrolling loader
        if (items.loadState.append is LoadState.Loading) {
            item { PokemonPlaceholder() }
        }

        // Infinite scrolling error
        if (items.loadState.append is LoadState.Error) {
            val error = items.loadState.append as LoadState.Error
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = error.error.localizedMessage ?: "Error loading more")
                }
            }
        }

        // Optional bottom spacer
        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

@Composable
private fun PokemonRow(
    index: Int,
    pokemon: PokemonListItemModel,
    navigateToPokemonDetail: (name: String, url: String) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(
            start = if(index % 2 == 0){0.dp}else{64.dp},
            end = if(index % 2 == 0){64.dp}else{0.dp}
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent, //Card content color,e.g.text
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navigateToPokemonDetail(pokemon.name, pokemon.url)
                }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if(index % 2 == 0) {
                Image(
                    painter = painterResource(id = R.drawable.pokeballart2),
                    contentDescription = "",
                    modifier = Modifier
                        .weight(0.3f) // proportion of the row's width
                        .fillMaxHeight()
                        .alpha(0.5f),  // adjust size
                    contentScale = ContentScale.Fit
                )
            }
            Text(
                modifier = Modifier
                    .padding(32.dp).weight(0.7f).fillMaxWidth(),
                text = pokemon.name.replaceFirstChar {
                    if (it.isLowerCase())
                        it.titlecase(Locale.getDefault())
                    else
                        it.toString()
                },
                style = MaterialTheme.typography.headlineSmall,
                textAlign = if(index % 2 == 0){
                    TextAlign.Start
                } else {
                    TextAlign.End
                }
            )
            if(index % 2 != 0) {
                Image(
                    painter = painterResource(id = R.drawable.pokeballart1),
                    contentDescription = "",
                    modifier = Modifier
                        .weight(0.3f) // proportion of the row's width
                        .fillMaxHeight()
                        .padding(top = 10.dp, bottom = 10.dp)
                        .alpha(0.5f),  // adjust size
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

@Composable
private fun PokemonPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
