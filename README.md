# FairLaunch

Application Android pour automatiser le lancement de Fairtiq basé sur la géolocalisation.

## Fonctionnalités

- **Carte interactive OpenStreetMap**: Zoom, déplacement, rotation
- **Gestion de points d'intérêt**:
  - Clic long sur la carte pour créer un point
  - Clic long sur un marqueur pour le supprimer
  - Stockage persistant des points en local (Room)
- **Suivi en arrière-plan**:
  - Vérification périodique de la position GPS
  - Détection de proximité configurable
  - Lancement automatique de l'application Fairtiq
  - Vibration du téléphone lors du déclenchement
- **Paramètres configurables**:
  - Fréquence de vérification de la position (secondes ou minutes)
  - Distance de proximité (mètres)
  - Activation/désactivation du suivi
- **Anti-spam**: Ne se déclenche qu'une fois par entrée de zone (nécessite de sortir et revenir)

## Architecture

Architecture Clean Architecture en 3 couches :

### Domain Layer (Kotlin pur)
- `model/` - Entités: MapPoint, AppSettings, ProximityState
- `repository/` - Interfaces des repositories
- `usecase/` - Logique métier: Add/Delete points, Check proximity, Settings management

### Data Layer
- `local/dao/` - Room DAOs (MapPointDao, ProximityStateDao)
- `local/entity/` - Entities Room
- `repository/` - Implémentations des repositories
- `mapper/` - Mappers Entity ↔ Domain

### App Layer (Presentation)
- `ui/map/` - Écran carte avec OpenStreetMap
- `ui/settings/` - Écran de paramètres
- `worker/` - WorkManager pour le suivi en arrière-plan
- `di/` - Modules Hilt

## Stack Technique

- **Langage**: Kotlin
- **UI**: Jetpack Compose + Material3
- **Architecture**: Clean Architecture (Domain/Data/App)
- **Carte**: OpenStreetMap (osmdroid)
- **Localisation**: Google Play Services Location
- **Tâches en arrière-plan**: WorkManager
- **DI**: Hilt
- **Async**: Coroutines + Flow
- **Database**: Room
- **Préférences**: DataStore

## Installation et Configuration

### Prérequis
- Android Studio Hedgehog ou plus récent
- SDK Android 24 minimum (Android 7.0)
- SDK Android 34 pour la compilation

### Build

```bash
# Cloner le repository
cd FairLaunch

# Build le projet
./gradlew build

# Assembler le debug APK
./gradlew assembleDebug

# Lancer les tests
./gradlew test
```

## Utilisation

1. **Première utilisation**:
   - L'application demande les permissions de localisation
   - Accepter les permissions FINE_LOCATION et BACKGROUND_LOCATION

2. **Créer des points**:
   - Appuyer longuement sur la carte pour créer un point
   - Les points apparaissent comme des marqueurs

3. **Supprimer des points**:
   - Appuyer longuement sur un marqueur pour le supprimer

4. **Configurer le suivi**:
   - Ouvrir les paramètres via l'icône en haut à droite
   - Configurer la fréquence de vérification (défaut: 300 secondes = 5 minutes)
   - Configurer la distance de proximité (défaut: 200 mètres)
   - Activer le suivi via le switch dans la barre du haut (il devient vert)

5. **Fonctionnement automatique**:
   - L'application vérifie votre position en arrière-plan
   - Quand vous entrez dans une zone (à la distance configurée):
     - Le téléphone vibre
     - L'application Fairtiq se lance automatiquement
   - Il faut sortir de la zone et y revenir pour déclencher à nouveau

## Permissions

- `ACCESS_FINE_LOCATION`: Localisation précise
- `ACCESS_COARSE_LOCATION`: Localisation approximative (fallback)
- `ACCESS_BACKGROUND_LOCATION`: Localisation en arrière-plan
- `VIBRATE`: Vibration du téléphone
- `INTERNET`: Chargement des tuiles de carte

## Structure du Projet

```
FairLaunch/
├── app/                    # UI layer
│   └── src/main/java/com/fairlaunch/
│       ├── di/            # Dependency injection
│       ├── worker/        # Background location worker (WorkManager)
│       ├── ui/
│       │   ├── map/       # Map screen
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

## Développement

Voir [AGENTS.md](AGENTS.md) pour les guidelines de développement détaillées.

## License

Projet personnel - Tous droits réservés
