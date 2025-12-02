# FairLaunch - Status & Notes

## ✅ Completed Features

### Core Functionality
- ✅ Clean Architecture implementation (Domain/Data/App layers)
- ✅ OpenStreetMap integration with user location
- ✅ Add/delete map points via long press
- ✅ Background location tracking with WorkManager
- ✅ Proximity detection with anti-spam (only triggers on zone entry)
- ✅ Auto-launch Fairtiq app when entering proximity zone
- ✅ Phone vibration on trigger
- ✅ Settings screen with configurable parameters
- ✅ Location permission handling
- ✅ Hilt dependency injection
- ✅ Room database for map points and proximity states
- ✅ DataStore for app settings

### UI Features
- ✅ Map centered on user location on startup
- ✅ Location tracking toggle in top bar
- ✅ Settings: check interval (in seconds for easy testing)
- ✅ Settings: proximity distance (in meters)
- ✅ Settings: enable/disable tracking

### Technical Implementation
- ✅ WorkManager with HiltWorkerFactory integration
- ✅ Short interval support (<15 min) via OneTimeWorkRequest with self-rescheduling
- ✅ Haversine formula for distance calculation (pure Kotlin)
- ✅ Proper state management with StateFlow
- ✅ Background service continues after app closure
- ✅ Fairtiq package name: `com.fairtiq.android` (verified and working)

## 📝 Optional Improvements

### User Experience
- [ ] Add launcher icons (currently using default)
- [ ] Implement two-step permission flow for background location (Android 10+)
  - First request foreground location
  - Then separately request background location with explanation
- [ ] Add visual feedback when proximity zone is triggered (notification)
- [ ] Show last check time in UI
- [ ] Add point names/labels on map
- [ ] Export/import points functionality

### Technical
- [ ] Add unit tests for ViewModels
- [ ] Add instrumented tests for database
- [ ] Implement proper error handling UI
- [ ] Add crash reporting (e.g., Firebase Crashlytics)
- [ ] Optimize battery usage analysis
- [ ] Add point clustering for better map performance with many points

### Production Readiness
- [ ] Convert check interval back to minutes for production (or add toggle)
- [ ] Add release build configuration
- [ ] Add ProGuard rules
- [ ] Implement proper logging (e.g., Timber)
- [ ] Add analytics events

## 🔧 Configuration

### Current Settings
- **Default check interval**: 300 seconds (5 minutes)
- **Default proximity distance**: 200 meters
- **Fairtiq package**: `com.fairtiq.android`
- **WorkManager**: Supports intervals from 15 seconds (testing) to any duration

### Testing the App
1. Open app and grant location permissions
2. Long press on map to create a point at your location
3. Go to Settings (top-right icon)
4. Set check interval to 30 seconds (for quick testing)
5. Set proximity distance to 200 meters
6. Enable tracking toggle in top bar (should turn green)
7. Watch logcat: `adb logcat | grep LocationCheckWorker`
8. Wait 30 seconds - you should see location checks in logs
9. If you're within 200m of your point, Fairtiq launches + phone vibrates

### Known Limitations
- WorkManager has a minimum 15-minute interval for PeriodicWork
  - Current implementation uses OneTimeWork with self-rescheduling for shorter intervals
  - Works well for testing, but may use slightly more battery
- No notification shown during background checks (by design, to avoid clutter)
- Background location accuracy depends on device GPS and Android battery optimization settings

## 📱 Permissions Required
- `ACCESS_FINE_LOCATION` - For precise GPS location
- `ACCESS_COARSE_LOCATION` - Fallback location
- `ACCESS_BACKGROUND_LOCATION` - To check location when app is closed
- `VIBRATE` - To vibrate phone on trigger
- `INTERNET` - For OpenStreetMap tiles
- `ACCESS_NETWORK_STATE` - Network status
- `WRITE_EXTERNAL_STORAGE` - For osmdroid cache (Android ≤12)

## 🏗️ Architecture

```
FairLaunch/
├── domain/          # Pure Kotlin - business logic
│   ├── model/       # MapPoint, AppSettings, ProximityState
│   ├── repository/  # Repository interfaces
│   └── usecase/     # Business logic use cases
├── data/            # Android - data layer
│   ├── local/       # Room database (DAOs, entities)
│   └── repository/  # Repository implementations
└── app/             # Android - UI layer
    ├── ui/          # Compose UI (MapScreen, SettingsScreen)
    ├── worker/      # Background WorkManager worker
    └── di/          # Hilt dependency injection
```

