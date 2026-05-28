# Android Architecture Decisions - Helix AI

## 1. Clean Architecture + MVVM
The app is organized into `core` and `features` packages.
- **Core**: Contains shared logic like networking, database, navigation, and UI theme.
- **Features**: Contains feature-specific UI and ViewModels (e.g., chat, search).

## 2. Jetpack Compose
100% declarative UI. We use a custom `HelixTheme` that enforces a "Dark Mode Only" policy as requested.

## 3. Dependency Injection with Hilt
Hilt is used for managing lifecycles of network clients, databases, and ViewModels. This ensures a scalable and testable codebase.

## 4. State Management with StateFlow
ViewModels expose UI state via `StateFlow`. Compose components observe this state, ensuring lifecycle-aware updates and efficient recomposition.

## 5. Navigation Compose
Type-safe navigation routes are defined in `Screen.kt`, and the navigation logic is centralized in `NavGraph.kt`.

## 6. Networking & Data
- **Retrofit/OkHttp**: Standard industry stack for API communication.
- **Room**: Local persistence for conversation history and messages.
- **DataStore**: Modern alternative to SharedPreferences for small key-value pairs like auth tokens.

## 7. Premium Minimal UI
The design system emphasizes:
- **Soft Dark Palette**: Deep blacks and dark grays to reduce eye strain and provide a premium feel.
- **Typography**: Clear, legible fonts with deliberate spacing.
- **Rounded Corners**: Modern, approachable aesthetic (16dp-24dp radius).
- **Mobile-First Spacing**: Generous padding for touch targets.

## 8. Authentication Flow
- **Token Persistence**: JWT tokens are securely stored using `androidx.datastore`.
- **Auto-Login**: `SplashScreen` checks for existing tokens to bypass the welcome screen.
- **Navigation Guards**: Screens are logically guarded by the authentication state.
- **Retrofit AuthService**: Type-safe client for all authentication endpoints.
