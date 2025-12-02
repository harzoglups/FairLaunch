# FairLaunch - Agent Guidelines

## Build Commands
- Build: `./gradlew build`
- Run tests: `./gradlew test`
- Run single test: `./gradlew test --tests "com.fairlaunch.domain.usecase.GetMapPointsUseCaseTest"`
- Run instrumented tests: `./gradlew connectedAndroidTest`
- Lint: `./gradlew lint`
- Assemble debug: `./gradlew assembleDebug`

## Architecture
Clean Architecture with 3 layers: **domain** (pure Kotlin), **data** (Android), **app** (UI)
- **Domain**: Business logic for map points, proximity detection, settings (no Android dependencies)
- **Data**: Repository implementations, Room database (points, proximity states), DataStore (settings)
- **App**: ViewModels (Hilt), Compose UI, OpenStreetMap integration, WorkManager background service

## Code Style
- **Language**: Kotlin with coroutines/Flow for async operations
- **Imports**: Group by (android, androidx, third-party, domain, data, ui), alphabetically sorted
- **Formatting**: 4 spaces indentation, 120 char line limit, trailing commas for multi-line
- **Types**: Explicit return types for public functions, type inference for local variables
- **Naming**: CamelCase for classes, camelCase for functions/variables, UPPER_SNAKE_CASE for constants
- **Nullability**: Prefer non-null types, use `?` explicitly when needed
- **Data classes**: Use for models/DTOs, implement mapping functions in separate mapper files
- **Sealed interfaces**: For UI states and navigation, prefer over sealed classes

## Error Handling
- Use `Result<T>` sealed class (Success/Error/Loading) for domain layer operations
- Catch exceptions in repository layer, never let them bubble to UI
- Display user-friendly error messages in UI, log technical details

## Dependency Injection
- Hilt for DI, annotate Application class with `@HiltAndroidApp`, Activity with `@AndroidEntryPoint`
- ViewModels with `@HiltViewModel`, inject use cases via constructor
- Provide dependencies in modules under `di/` package: DatabaseModule, AppModule
- Use `@HiltWorker` for WorkManager workers

## Location & Maps
- OpenStreetMap via osmdroid for map display
- Google Play Services Location for GPS access
- WorkManager for periodic background location checks
- Permissions: FINE_LOCATION, COARSE_LOCATION, BACKGROUND_LOCATION, VIBRATE

## Background Service
- LocationCheckWorker runs periodically based on user settings
- Checks proximity to all saved points
- Launches Fairtiq app and vibrates when entering a zone
- Tracks proximity state per point to avoid repeated triggers

## Testing
- Unit tests in `domain/` use JUnit + MockK, coroutines test with `kotlinx-coroutines-test`
- Repository tests mock DAO, verify mapping and error handling
- ViewModel tests use Turbine for Flow testing, verify state transitions
