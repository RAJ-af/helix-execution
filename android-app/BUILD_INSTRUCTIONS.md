# Helix AI - MVP Build & Release Guide

This guide describes how to build and install the first runnable APK for Helix AI.

## 1. Environment Setup
- **JDK**: Version 17 is required.
- **Android SDK**: API Level 34 (UpsideDownCake) or newer.
- **Gradle**: 8.2 (handled by wrapper).

## 2. Configuration
Copy `.env.example` to a new file called `.env` in the `android-app` root.
Update `BASE_URL` to point to your running Helix Backend.

## 3. Running the Build
Open a terminal in the `android-app` folder and run:
```bash
./gradlew assembleDebug
```

## 4. Retrieving the APK
Once the build finishes, the APK will be located at:
`android-app/app/build/outputs/apk/debug/app-debug.apk`

I have created a `release/` folder in the project root where you should move your final tested builds.

## 5. Backend Connection
Ensure your backend is running:
```bash
cd backend
docker-compose up --build
```

The app is pre-configured with a "Mock Mode" for AI providers if API keys are missing, allowing you to test the full UI flow (Chat -> Search -> Tools -> Agent) immediately.
