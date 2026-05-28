# Helix AI Android App

Native Android foundation for Helix AI, built with modern Android development practices.

## Tech Stack
- **Kotlin**: Primary programming language.
- **Jetpack Compose**: Modern toolkit for building native UI.
- **Hilt**: Dependency injection for Android.
- **Navigation Compose**: Navigation between screens.
- **Room**: Local database for persistence.
- **Retrofit & OkHttp**: Type-safe HTTP client for Android and Java.
- **DataStore**: Modern data storage solution.
- **Coroutines & StateFlow**: Asynchronous programming and state management.

## Project Structure
```
app/src/main/java/com/helix/app/
├── core/                # Shared modules
│   ├── data/            # Local & Remote data sources
│   ├── di/              # Hilt Modules
│   ├── navigation/      # NavGraph and Routes
│   └── ui/              # Theme and Components
└── features/            # Feature-based screens
    ├── home/
    ├── chat/
    └── search/
```

## UI & Design
- **Premium Minimal**: Focused on content and ease of use.
- **Dark Mode Only**: Optimized for modern OLED displays.
- **Material 3**: Utilizing the latest design components from Google.

## Setup
1. **Open in Android Studio**: Ensure you have the latest Hedgehog or Iguana version.
2. **Sync Gradle**: The project uses Version Catalogs (`libs.versions.toml`).
3. **Run**: The app is pre-configured to connect to the backend at `http://10.0.2.2:8000` (Android Emulator default localhost).

## Key Features Implemented
- **Splash Animation**: Smooth entry into the app.
- **Navigation Graph**: Ready-to-use routes for all major screens.
- **Hilt DI**: Pre-configured modules for Database and Network.
- **Reusable UI**: Custom buttons, cards, and theme constants.
```
