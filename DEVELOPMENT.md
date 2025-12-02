# FairLaunch - Development Notes

## Quick Start pour Nouvelle Session

### Build et Installation
```bash
# Build avec Java de Android Studio
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew assembleDebug

# Installation sur device
~/Library/Android/sdk/platform-tools/adb install -r app/build/outputs/apk/debug/app-debug.apk

# Lancer l'app
~/Library/Android/sdk/platform-tools/adb shell am start -n com.fairlaunch/.MainActivity
```

### Debugging
```bash
# Voir les logs du Worker
~/Library/Android/sdk/platform-tools/adb logcat | grep LocationCheckWorker

# Effacer et voir les logs
~/Library/Android/sdk/platform-tools/adb logcat -c && ~/Library/Android/sdk/platform-tools/adb logcat | grep LocationCheckWorker

# Vérifier si device connecté
~/Library/Android/sdk/platform-tools/adb devices

# Vérifier WorkManager diagnostics
~/Library/Android/sdk/platform-tools/adb shell am broadcast -a "androidx.work.diagnostics.REQUEST_DIAGNOSTICS" -p com.fairlaunch
```

## Décisions Techniques Importantes

### 1. Fairtiq Package Name
- **Package utilisé** : `com.fairtiq.android` (vérifié et fonctionnel)
- **Localisation** : `LocationCheckWorker.kt` ligne 34
- Testé avec : `adb shell monkey -p com.fairtiq.android -c android.intent.category.LAUNCHER 1`

### 2. Intervalles de Vérification
- **Format** : Secondes (pour faciliter les tests)
- **Défaut** : 300 secondes (5 minutes)
- **WorkManager limitation** : Minimum 15 minutes pour PeriodicWork
- **Solution** : Pour intervalles < 900s, utilise OneTimeWorkRequest avec auto-rescheduling
- **Localisation** : `LocationWorkScheduler.kt` et `LocationCheckWorker.rescheduleIfNeeded()`

### 3. Hilt + WorkManager
- **Configuration obligatoire** :
  - `FairLaunchApplication` implements `Configuration.Provider`
  - Injection de `HiltWorkerFactory`
  - Désactivation de l'init auto de WorkManager dans `AndroidManifest.xml`
- **Localisation** : `FairLaunchApplication.kt`, `AndroidManifest.xml` (meta-data avec `tools:node="remove"`)

### 4. Calcul de Distance
- **Méthode** : Formule de Haversine (pure Kotlin)
- **Raison** : Éviter dépendance Android dans domain layer
- **Localisation** : `CheckProximityUseCase.calculateDistance()`

### 5. Anti-Spam Proximity
- **Mécanisme** : Stockage de l'état (inside/outside) par point dans Room
- **Déclenchement** : Seulement lors du passage outside → inside
- **Base de données** : Table `proximity_states` avec `point_id` et `is_inside`

### 6. Carte et Localisation
- **Centrage auto** : `locationOverlay.runOnFirstFix` + `controller.animateTo()`
- **Follow mode** : `enableFollowLocation()` activé
- **Zoom initial** : 15 (plus proche)

## Structure des Données

### DataStore (Settings)
```kotlin
check_interval_seconds: Int = 300    // Changé de minutes à secondes
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

## Points d'Attention

### Pour Production
1. **Permissions** : Implémenter flow en 2 étapes pour BACKGROUND_LOCATION (Android 10+)
2. **Intervalle** : Possibilité de revenir aux minutes (ou garder secondes avec validation min/max)
3. **Battery optimization** : Tester sur différents devices avec Doze mode
4. **Icons** : Ajouter launcher icons personnalisés

### Limitations Connues
1. WorkManager PeriodicWork minimum = 15 minutes
2. Pas de notification pendant les checks (by design)
3. Précision GPS dépend des paramètres d'économie d'énergie de l'appareil

## Tests Effectués

### Tests Fonctionnels ✅
- [x] Création/suppression de points sur carte
- [x] Centrage automatique sur position utilisateur
- [x] Activation/désactivation du tracking
- [x] Modification intervalle et distance
- [x] Worker se lance à intervalles courts (30-60s)
- [x] Détection de proximité
- [x] Lancement Fairtiq
- [x] Vibration téléphone
- [x] Anti-spam (1 trigger par entrée de zone)
- [x] Persistence après fermeture app
- [x] Logs détaillés dans LocationCheckWorker

### Tests Techniques ✅
- [x] Build Gradle
- [x] Injection Hilt
- [x] Room migrations (none needed yet)
- [x] DataStore persistence
- [x] WorkManager scheduling
- [x] OneTimeWork rescheduling

## Logs Typiques

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

### Premier Commit
Commit effectué avec toutes les fonctionnalités principales.

### Fichiers Importants
- `README.md` - Documentation utilisateur
- `AGENTS.md` - Guidelines pour agents IA
- `TODO.md` - Status du projet + roadmap
- `DEVELOPMENT.md` - Ce fichier (notes techniques)

## Contact / Support
Projet personnel - Sylvain
