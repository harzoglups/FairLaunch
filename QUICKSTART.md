# AutoTiq - Quick Start

## 🚀 For a New Development Session

### 1. Check Environment

```bash
# Android device connected?
~/Library/Android/sdk/platform-tools/adb devices

# Should show a device (not "unauthorized")
# If unauthorized, accept on the phone
```

### 2. Build & Install

```bash
# Go to the project
cd /Users/sylvain/AndroidStudioProjects/AutoTiq

# Build (with Android Studio Java)
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew assembleDebug

# Install on device
~/Library/Android/sdk/platform-tools/adb install -r app/build/outputs/apk/debug/app-debug.apk

# Launch the app
~/Library/Android/sdk/platform-tools/adb shell am start -n com.autotiq/.MainActivity
```

### 3. Test the Worker

```bash
# View Worker logs in real-time
~/Library/Android/sdk/platform-tools/adb logcat -c
~/Library/Android/sdk/platform-tools/adb logcat | grep LocationCheckWorker

# In the app:
# 1. Create a point (long press on map)
# 2. Configure time window in the edit dialog
# 3. Settings (bottom-right button) → Set interval to 30 seconds
# 4. Enable tracking switch
# 5. Wait 30 seconds → Logs should appear
```

### 4. Expected Logs (If Everything Works)

```
LocationCheckWorker: Starting location check...
LocationCheckWorker: Settings: interval=30s, distance=200m, enabled=true
LocationCheckWorker: Current location: 43.342..., 1.520...
LocationCheckWorker: No proximity zones entered
LocationCheckWorker: Rescheduling next check in 30s
```

## 📚 Available Documentation

| File | Description |
|---------|-------------|
| **README.md** | General documentation, architecture, usage |
| **AGENTS.md** | Guidelines for AI agents (build, architecture, style) |
| **TODO.md** | Project status, completed features, future improvements |
| **DEVELOPMENT.md** | Detailed technical notes, important decisions |
| **QUICKSTART.md** | This file - quick start |
| **.env.example** | Environment variables |

## 🔧 Useful Commands

### Debug
```bash
# Clear logs
~/Library/Android/sdk/platform-tools/adb logcat -c

# View all app logs
~/Library/Android/sdk/platform-tools/adb logcat | grep autotiq

# WorkManager diagnostics
~/Library/Android/sdk/platform-tools/adb shell am broadcast -a "androidx.work.diagnostics.REQUEST_DIAGNOSTICS" -p com.autotiq
```

### Build
```bash
# Complete build (with tests)
./gradlew build

# Only tests
./gradlew test

# Clean + build
./gradlew clean assembleDebug

# View available tasks
./gradlew tasks
```

### Installation
```bash
# Install (overwrites existing version)
~/Library/Android/sdk/platform-tools/adb install -r app/build/outputs/apk/debug/app-debug.apk

# Uninstall
~/Library/Android/sdk/platform-tools/adb uninstall com.autotiq

# Launch Fairtiq (for testing)
~/Library/Android/sdk/platform-tools/adb shell monkey -p com.fairtiq.android -c android.intent.category.LAUNCHER 1
```

## ⚠️ Troubleshooting

### "Unable to locate a Java Runtime"
```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
```

### "Device not found"
```bash
# Restart ADB server
killall adb
~/Library/Android/sdk/platform-tools/adb devices
```

### "Worker doesn't start"
- Check that tracking is enabled in Settings
- Check in Settings that the interval is configured
- View logs: `adb logcat | grep -E "(WorkManager|LocationCheckWorker)"`
- The app must have location permissions

### "Fairtiq doesn't launch"
- Check that Fairtiq is installed: 
  ```bash
  ~/Library/Android/sdk/platform-tools/adb shell pm list packages | grep fairtiq
  # Should display: package:com.fairtiq.android
  ```

## 🎯 Current Project Status

✅ **Complete and functional application**

- Full-screen immersive map with floating action buttons
- Interactive map with create/delete/edit points
- Editable marker properties (name, time window with HH:MM precision)
- Background service with WorkManager
- Anti-spam proximity detection
- Time-based activation (points only trigger within their time window)
- Automatic Fairtiq launch + vibration
- Configurable settings (interval in seconds, distance)
- Data persistence (Room + DataStore)
- Clean Architecture with Hilt

**Ready for**: Real testing, add icons, optimizations

See **TODO.md** for optional future improvements.
