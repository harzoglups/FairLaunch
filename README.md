# FairLaunch

Android application to automate Fairtiq app launch based on geolocation.

## Features

- **Interactive OpenStreetMap**: Zoom, pan, rotation
- **Map Layer Selection**: Switch between Street and Topographic views via floating button
- **Points of Interest Management**:
  - Long press on map to create a point
  - Long press on marker to delete it
  - Visual proximity circles (red semi-transparent) around each point
  - Persistent local storage of points (Room)
- **Background Tracking**:
  - Periodic GPS position checking
  - Configurable proximity detection
  - Automatic launch of Fairtiq application
  - Phone vibration on trigger
  - Auto-start on device boot
- **Configurable Settings**:
  - Position check frequency (seconds or minutes)
  - Proximity distance (meters)
  - Enable/disable tracking
- **Anti-spam**: Only triggers once per zone entry (requires leaving and returning)

## Architecture

Clean Architecture with 3 layers:

### Domain Layer (Pure Kotlin)
- `model/` - Entities: MapPoint, AppSettings, ProximityState
- `repository/` - Repository interfaces
- `usecase/` - Business logic: Add/Delete points, Check proximity, Settings management

### Data Layer
- `local/dao/` - Room DAOs (MapPointDao, ProximityStateDao)
- `local/entity/` - Room entities
- `repository/` - Repository implementations
- `mapper/` - Entity ↔ Domain mappers

### App Layer (Presentation)
- `ui/map/` - Map screen with OpenStreetMap
- `ui/settings/` - Settings screen
- `worker/` - WorkManager for background tracking
- `di/` - Hilt modules

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material3
- **Architecture**: Clean Architecture (Domain/Data/App)
- **Map**: OpenStreetMap (osmdroid)
- **Location**: Google Play Services Location
- **Background Tasks**: WorkManager
- **DI**: Hilt
- **Async**: Coroutines + Flow
- **Database**: Room
- **Preferences**: DataStore
- **Icon**: Custom vector drawable (red "L" on adaptive icon)

## Installation and Setup

### Prerequisites
- Android Studio Hedgehog or newer
- Android SDK 24 minimum (Android 7.0)
- Android SDK 34 for compilation

### Build

```bash
# Clone the repository
cd FairLaunch

# Build the project
./gradlew build

# Assemble debug APK
./gradlew assembleDebug

# Run tests
./gradlew test
```

## Usage

1. **First Use**:
   - The app requests location permissions - accept to use the app
   - The app requests notification permissions (Android 13+) - **required for alerts**
   - Accept BACKGROUND_LOCATION in settings to enable background tracking

2. **Create Points**:
   - Long press (500ms) on the map to create a point
   - Points appear as markers with proximity circles

3. **Delete Points**:
   - Long press (500ms) on a marker to delete it
   - Markers have a large touch zone for easy interaction

4. **Configure Tracking**:
   - Open settings via the top-right icon
   - Configure check frequency (default: 300 seconds = 5 minutes)
   - Configure proximity distance (default: 200 meters)
   - Enable tracking via the switch in the top bar (turns green)

5. **Change Map View**:
   - Tap the floating layers button in the top-right corner of the map
   - Select desired map type: Street or Topographic
   - Selection is saved automatically

6. **Automatic Operation**:
   - The app checks your position in the background
   - Red circles on the map show proximity zones
   - When you enter a zone (at configured distance):
     - The phone vibrates (strong 3-burst pattern)
     - The screen wakes up if locked
     - The Fairtiq app launches automatically
   - You must leave and re-enter the zone to trigger again
   - **Tracking persists after device reboot** (auto-restart if enabled)

## Permissions

- `ACCESS_FINE_LOCATION`: Precise location
- `ACCESS_COARSE_LOCATION`: Approximate location (fallback)
- `ACCESS_BACKGROUND_LOCATION`: Background location
- `RECEIVE_BOOT_COMPLETED`: Auto-start tracking on device boot
- `POST_NOTIFICATIONS`: Notification alerts (Android 13+)
- `USE_FULL_SCREEN_INTENT`: Wake screen when entering zone
- `VIBRATE`: Phone vibration
- `INTERNET`: Map tiles loading

## Project Structure

```
FairLaunch/
├── app/                    # UI layer
│   └── src/main/java/com/fairlaunch/
│       ├── di/            # Dependency injection
│       ├── receiver/      # Boot receiver for auto-start
│       ├── worker/        # Background location worker (WorkManager)
│       ├── ui/
│       │   ├── map/       # Map screen with proximity circles
│       │   ├── settings/  # Settings screen
│       │   └── theme/     # Compose theme
│       └── MainActivity.kt
├── data/                   # Data layer
│   └── src/main/java/com/fairlaunch/data/
│       ├── local/         # Room database
│       ├── repository/    # Repository implementations
│       └── mapper/        # Mappers
└── domain/                 # Domain layer (pure Kotlin)
    └── src/main/java/com/fairlaunch/domain/
        ├── model/         # Domain entities
        ├── repository/    # Repository interfaces
        └── usecase/       # Business logic
```

## Development

See [AGENTS.md](AGENTS.md) for detailed development guidelines.

## License

Personal project - All rights reserved
