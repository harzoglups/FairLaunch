# FairLaunch - Development Notes

## Quick Start for New Session

### Build and Installation
```bash
# Build with Android Studio Java
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew assembleDebug

# Install on device
~/Library/Android/sdk/platform-tools/adb install -r app/build/outputs/apk/debug/app-debug.apk

# Launch the app
~/Library/Android/sdk/platform-tools/adb shell am start -n com.fairlaunch/.MainActivity
```

### Debugging
```bash
# View Worker logs
~/Library/Android/sdk/platform-tools/adb logcat | grep LocationCheckWorker

# Clear and view logs
~/Library/Android/sdk/platform-tools/adb logcat -c && ~/Library/Android/sdk/platform-tools/adb logcat | grep LocationCheckWorker

# Check if device is connected
~/Library/Android/sdk/platform-tools/adb devices

# Check WorkManager diagnostics
~/Library/Android/sdk/platform-tools/adb shell am broadcast -a "androidx.work.diagnostics.REQUEST_DIAGNOSTICS" -p com.fairlaunch
```

## Important Technical Decisions

### 1. Fairtiq Package Name
- **Package used**: `com.fairtiq.android` (verified and working)
- **Location**: `LocationCheckWorker.kt` line 34
- Tested with: `adb shell monkey -p com.fairtiq.android -c android.intent.category.LAUNCHER 1`

### 2. Check Intervals
- **Format**: Seconds (to ease testing)
- **Default**: 300 seconds (5 minutes)
- **WorkManager limitation**: Minimum 15 minutes for PeriodicWork
- **Solution**: For intervals < 900s, uses OneTimeWorkRequest with auto-rescheduling
- **Location**: `LocationWorkScheduler.kt` and `LocationCheckWorker.rescheduleIfNeeded()`

### 3. Hilt + WorkManager
- **Required configuration**:
  - `FairLaunchApplication` implements `Configuration.Provider`
  - `HiltWorkerFactory` injection
  - Disable WorkManager auto-init in `AndroidManifest.xml`
- **Location**: `FairLaunchApplication.kt`, `AndroidManifest.xml` (meta-data with `tools:node="remove"`)

### 4. Distance Calculation
- **Method**: Haversine formula (pure Kotlin)
- **Reason**: Avoid Android dependency in domain layer
- **Location**: `CheckProximityUseCase.calculateDistance()`

### 5. Anti-Spam Proximity
- **Mechanism**: Store state (inside/outside) per point in Room
- **Trigger**: Only on outside → inside transition
- **Database**: Table `proximity_states` with `point_id` and `is_inside`

### 6. Map and Location
- **Auto-centering**: `locationOverlay.runOnFirstFix` + `controller.animateTo()`
- **Follow mode**: `enableFollowLocation()` enabled
- **Initial zoom**: 15 (closer)

## Data Structure

### DataStore (Settings)
```kotlin
check_interval_seconds: Int = 300    // Changed from minutes to seconds
proximity_distance_meters: Int = 200
location_tracking_enabled: Boolean = false
```

### Room Database
**Table: map_points**
- id (PK, auto-increment)
- latitude (Double)
- longitude (Double)
- created_at (Long)

**Table: proximity_states**
- point_id (PK, FK → map_points)
- is_inside (Boolean)

## Points of Attention

### For Production
1. **Permissions**: Implement 2-step flow for BACKGROUND_LOCATION (Android 10+)
2. **Interval**: Option to revert to minutes (or keep seconds with min/max validation)
3. **Battery optimization**: Test on different devices with Doze mode
4. **Icons**: Add custom launcher icons

### Known Limitations
1. WorkManager PeriodicWork minimum = 15 minutes
2. No notification during checks (by design)
3. GPS accuracy depends on device power-saving settings

## Tests Performed

### Functional Tests ✅
- [x] Create/delete points on map
- [x] Automatic centering on user position
- [x] Enable/disable tracking
- [x] Modify interval and distance
- [x] Worker runs at short intervals (30-60s)
- [x] Proximity detection
- [x] Fairtiq launch
- [x] Phone vibration
- [x] Anti-spam (1 trigger per zone entry)
- [x] Persistence after app closure
- [x] Detailed logs in LocationCheckWorker

### Technical Tests ✅
- [x] Gradle build
- [x] Hilt injection
- [x] Room migrations (none needed yet)
- [x] DataStore persistence
- [x] WorkManager scheduling
- [x] OneTimeWork rescheduling

## Typical Logs

### Worker Success
```
LocationCheckWorker: Starting location check...
LocationCheckWorker: Settings: interval=30s, distance=200m, enabled=true
LocationCheckWorker: Current location: 43.3423324, 1.5203386
LocationCheckWorker: No proximity zones entered
LocationCheckWorker: Rescheduling next check in 30s
```

### Proximity Triggered
```
LocationCheckWorker: Entered proximity zone for 1 point(s)
LocationCheckWorker: Launched Fairtiq app
LocationCheckWorker: Vibration triggered
```

### Worker Error
```
LocationCheckWorker: Location permission not granted
LocationCheckWorker: Error during location check
```

## Git

### First Commit
Commit made with all main features.

### Important Files
- `README.md` - User documentation
- `AGENTS.md` - Guidelines for AI agents
- `TODO.md` - Project status + roadmap
- `DEVELOPMENT.md` - This file (technical notes)

## Contact / Support
Personal project - Sylvain
