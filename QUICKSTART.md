# FairLaunch - Quick Start

## 🚀 Pour une Nouvelle Session de Développement

### 1. Vérifier l'Environnement

```bash
# Device Android connecté ?
~/Library/Android/sdk/platform-tools/adb devices

# Doit afficher un device (pas "unauthorized")
# Si unauthorized, accepter sur le téléphone
```

### 2. Build & Install

```bash
# Aller dans le projet
cd /Users/sylvain/AndroidStudioProjects/FairLaunch

# Build (avec Java de Android Studio)
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew assembleDebug

# Installer sur le device
~/Library/Android/sdk/platform-tools/adb install -r app/build/outputs/apk/debug/app-debug.apk

# Lancer l'app
~/Library/Android/sdk/platform-tools/adb shell am start -n com.fairlaunch/.MainActivity
```

### 3. Tester le Worker

```bash
# Voir les logs du Worker en temps réel
~/Library/Android/sdk/platform-tools/adb logcat -c
~/Library/Android/sdk/platform-tools/adb logcat | grep LocationCheckWorker

# Dans l'app :
# 1. Créer un point (long press sur la carte)
# 2. Settings → Mettre intervalle à 30 secondes
# 3. Activer le switch (barre du haut)
# 4. Attendre 30 secondes → Logs devraient apparaître
```

### 4. Logs Attendus (Si Tout Fonctionne)

```
LocationCheckWorker: Starting location check...
LocationCheckWorker: Settings: interval=30s, distance=200m, enabled=true
LocationCheckWorker: Current location: 43.342..., 1.520...
LocationCheckWorker: No proximity zones entered
LocationCheckWorker: Rescheduling next check in 30s
```

## 📚 Documentation Disponible

| Fichier | Description |
|---------|-------------|
| **README.md** | Documentation générale, architecture, utilisation |
| **AGENTS.md** | Guidelines pour agents IA (build, architecture, style) |
| **TODO.md** | Status du projet, features complétées, améliorations futures |
| **DEVELOPMENT.md** | Notes techniques détaillées, décisions importantes |
| **QUICKSTART.md** | Ce fichier - démarrage rapide |
| **.env.example** | Variables d'environnement |

## 🔧 Commandes Utiles

### Debug
```bash
# Effacer les logs
~/Library/Android/sdk/platform-tools/adb logcat -c

# Voir tous les logs de l'app
~/Library/Android/sdk/platform-tools/adb logcat | grep fairlaunch

# Diagnostics WorkManager
~/Library/Android/sdk/platform-tools/adb shell am broadcast -a "androidx.work.diagnostics.REQUEST_DIAGNOSTICS" -p com.fairlaunch
```

### Build
```bash
# Build complet (avec tests)
./gradlew build

# Seulement les tests
./gradlew test

# Clean + build
./gradlew clean assembleDebug

# Voir les tâches disponibles
./gradlew tasks
```

### Installation
```bash
# Installer (écrase version existante)
~/Library/Android/sdk/platform-tools/adb install -r app/build/outputs/apk/debug/app-debug.apk

# Désinstaller
~/Library/Android/sdk/platform-tools/adb uninstall com.fairlaunch

# Lancer Fairtiq (pour tester)
~/Library/Android/sdk/platform-tools/adb shell monkey -p com.fairtiq.android -c android.intent.category.LAUNCHER 1
```

## ⚠️ Troubleshooting

### "Unable to locate a Java Runtime"
```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
```

### "Device not found"
```bash
# Redémarrer serveur ADB
killall adb
~/Library/Android/sdk/platform-tools/adb devices
```

### "Worker ne se lance pas"
- Vérifier que le switch est bien VERT (activé)
- Vérifier dans Settings que l'intervalle est configuré
- Regarder les logs : `adb logcat | grep -E "(WorkManager|LocationCheckWorker)"`
- L'app doit avoir les permissions de localisation

### "Fairtiq ne se lance pas"
- Vérifier que Fairtiq est bien installé : 
  ```bash
  ~/Library/Android/sdk/platform-tools/adb shell pm list packages | grep fairtiq
  # Doit afficher : package:com.fairtiq.android
  ```

## 🎯 État Actuel du Projet

✅ **Application complète et fonctionnelle**

- Carte interactive avec création/suppression de points
- Background service avec WorkManager
- Détection de proximité anti-spam
- Lancement automatique de Fairtiq + vibration
- Paramètres configurables (intervalle en secondes, distance)
- Persistance des données (Room + DataStore)
- Architecture Clean avec Hilt

**Prêt pour** : Tests réels, ajout d'icônes, optimisations

Voir **TODO.md** pour les améliorations futures optionnelles.
