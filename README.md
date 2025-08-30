
# Pokedex App

A clean architecture Android application to browse Pokémon using the [PokéAPI](https://pokeapi.co/).
Built with Kotlin, Jetpack Compose, Retrofit, Paging, Hilt, Room (optional), and Coroutine Flows.

---

## Project Architecture

The app is divided into **Clean Architecture layers**:

```
- core/
  - network/
    - API.kt
    - PokemonApiService.kt
    - util/
      - APIResult.kt
- pokemonlist/
  - data/
    - network/
      - model/
        - PokemonListResponse.kt
        - PokemonResponse.kt
        - AbilityResponse.kt
        - MoveResponse.kt
        - StatResponse.kt
        - TypeResponse.kt
      - datasource/
        - PokemonPagingSource.kt
      - PokemonApiService.kt
    - repository/
      - PokemonRepositoryImpl.kt
  - domain/
    - model/
      - PokemonListItemModel.kt
      - UIState.kt
    - repository/
      - PokemonRepository.kt
  - presentation/
    - PokemonListScreen.kt
    - PokemonListViewModel.kt
- pokemondetail/
  - data/
    - mapper/
      - PokemonMapper.kt
    - network/
      - PokemonApiService.kt
  - domain/
    - entity/
      - PokemonUiEntity.kt
      - CustomPokemonAPIResource.kt
      - PokemonAbilityUiEntity.kt
      - PokemonMoveUiEntity.kt
      - PokemonSpriteUiEntity.kt
      - PokemonStatUiEntity.kt
      - PokemonTypesUiEntity.kt
    - model/
      - PokemonDetailResult.kt
    - uistate/
      - PokemonDetailUiState.kt
    - usecase/
      - GetPokemonDetailUseCase.kt
  - presentation/
    - PokemonDetailScreen.kt
    - PokemonDetailViewModel.kt
```

---

## Core Layer

### `APIResult.kt`

A sealed class for network responses:

```kotlin
sealed class APIResult<out T> {
    data class Success<out T>(val data: T): APIResult<T>()
    data class Failure(
        val throwable: Throwable,
        val message: String? = throwable.localizedMessage,
        val code: Int? = null
    ) : APIResult<Nothing>()
    data object Loading : APIResult<Nothing>()
}
```

---

## Pokémon List Feature

### Repository

`PokemonRepositoryImpl.kt` handles data fetching via `PokemonApiService`.

### Paging

`PokemonPagingSource.kt` implements pagination for the Pokémon list with `Infinite Scrolling`.

### Presentation

`PokemonListScreen.kt` uses a `LazyColumn` of `Card` items:

```kotlin
Card(
    modifier = Modifier.fillMaxWidth(),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { navigateToPokemonDetail(pokemon.name, pokemon.url) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = pokemon.name, style = MaterialTheme.typography.bodyLarge)
    }
}
```

---

## Pokémon Detail Feature

### Data Layer

`PokemonResponse.kt` and nested responses (`AbilityResponse`, `MoveResponse`, etc.) mirror the API JSON structure.

`PokemonMapper.kt` maps `PokemonResponse` → `PokemonUiEntity`.

### Domain Layer

- **Entities**: `PokemonUiEntity` and nested classes (`PokemonAbilityUiEntity`, `PokemonMoveUiEntity`, etc.)
- **Result**: `PokemonDetailResult`
- **UI State**: `PokemonDetailUiState` (contains `PokemonUiEntity`, `UIState`, and error messages)
- **UseCase**: `GetPokemonDetailUseCase` emits `Flow<PokemonDetailUiState>`

### Presentation Layer

`PokemonDetailScreen.kt`:

```kotlin
val state by viewModel.pokemonDetailState.collectAsStateWithLifecycle()
LaunchedEffect(key1 = pokemonName) {
    viewModel.getPokemonDetail(pokemonName)
}

when(state.uiState) {
    UIState.LOADING -> CircularProgressIndicator()
    UIState.SUCCESS -> Text(state.pokemon.toString())
    UIState.FAILED -> Text(state.errorMessage ?: "Unknown error")
}
```

`PokemonDetailViewModel.kt` holds a `MutableStateFlow<PokemonDetailUiState>` and exposes `StateFlow` to the UI.

---

## Dependencies

- Kotlin 1.8.x
- Jetpack Compose
- Retrofit + Kotlinx Serialization
- Paging 3
- Hilt for DI
- Coroutines + Flow

---

## Testing

There is also Test coverage for `PokemonList` feature
Covering Unit-testing and Integration-Tests

```
- pokemonlist/
  - data/
    - network/
      - datasource/
        - PokemonPagingSourceTest.kt
      - PokemonApiServiceTest.kt
    - repository/
      - PokemonRepositoryImpl.kt
  - domain/
    - model/
      - mapper
        - PokemonListMapperTest.kt
      - usecase/
        - GetPokemonListPagerUseCase.kt
```

---

## Notes

- **Clean Architecture**: repository layer never leaks `APIResult` to use cases.
- **UseCase** handles business logic, mapping API models to UI entities.
- **UI** observes `StateFlow` to react to loading, success, or error states.
- **Mapper Layer** centralizes transformations from API models to UI entities.
- **Text-Coverage** Unit-testing and Integration-Testing
