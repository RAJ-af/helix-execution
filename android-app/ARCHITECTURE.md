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

## 9. Chat Implementation
- **Room-First Caching**: Conversations and messages are cached locally for offline access and instant loading.
- **Bi-directional Sync**: Repositories synchronize local data with the remote API.
- **Premium UI Components**: Custom `MessageBubble` and `ChatInputBar` with keyboard-aware layouts.
- **Smooth Interaction**: Auto-scrolling, loading states, and animations provide a production-quality feel.

## 10. Mobile Streaming Engine
- **OkHttp SSE**: Robust client for handling text/event-stream connections.
- **Incremental State Updates**: `ChatViewModel` processes `message_delta` events to provide real-time UI updates without full screen recomposition.
- **Resilient Connections**: Configured with extended timeouts for stable streaming over mobile networks.
- **Smooth Auto-Scroll**: The chat list automatically follows the streaming content for a premium experience.

## 11. Search & Source Visualization
- **Source Cards**: Minimal cards for quick reference to external search results.
- **Incremental Search State**: UI reflects search progress (Searching -> Sources -> Streaming).
- **Follow-up Integration**: Chip-based interaction for continuous exploration.
- **Rich Media Handling**: Support for favicons and domain-based source identification.

## 12. Integrated Terminal & Tool Visualization
- **Terminal UI**: Monospace output rendering with live streaming updates.
- **Tool Cards**: High-level execution status with expandable terminal views.
- **Monospace Typography**: Optimized for code and command readability on mobile.
- **UX Parity**: Follows the Claude Code / cursor-style execution flow for a premium developer feel.

## 13. Agent Progress & Clarification UI
- **Task Checklist**: A live-updating progress card showing the status of each agent step with smooth animations.
- **Integrated MCQ**: One-tap clarification chips allow users to guide the agent without typing.
- **Agent Status Lifecycle**: UI adapts based on the agent's state (Planning, Running, Waiting for Input, Completed).
- **Execution History**: Full visibility into tool calls and search operations performed during a complex task.

## 14. AI Provider & Model Management
- **Model Selector**: Premium bottom sheet for on-the-fly model switching with capability indicators.
- **Global Settings**: DataStore integration for persisting user-preferred AI providers.
- **Active Model Indicator**: Visual feedback in the Chat UI showing which engine is currently powering the conversation.
- **Mobile-First Settings**: Clean, intuitive interface for managing API keys and local/cloud preferences.

## 15. Dynamic AI Configuration
- **SettingsManager**: Persists user preferences for AI models and providers using DataStore.
- **Model Switching UI**: Provides a premium bottom sheet for on-the-fly selection of different AI engines.
- **Provider-Agnostic UI**: The frontend logic remains decoupled from specific AI provider implementations, relying solely on unified SSE events.
