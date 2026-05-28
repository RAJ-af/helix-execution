# Helix AI - Android Build Instructions

Follow these steps to generate a runnable APK for MVP testing.

## Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer.
- JDK 17.
- Gradle 8.2.

## Local Configuration
1. Open the project in Android Studio.
2. Ensure you have a running backend. By default, the app points to `http://10.0.2.2:8000` (local machine from emulator).
3. If using a real device, update `Config.BASE_URL` in `com.helix.app.core.common.Config` to your machine's IP.

## Building the APK
### Via Android Studio
1. Go to `Build` > `Build Bundle(s) / APK(s)` > `Build APK(s)`.
2. Once complete, the APK will be located in `app/build/outputs/apk/debug/app-debug.apk`.

### Via Command Line
Run the following from the `android-app` root:
```bash
./gradlew assembleDebug
```

## Deployment
1. Transfer the `app-debug.apk` to your Android device.
2. Enable "Install from Unknown Sources" in your device settings.
3. Install and launch Helix AI.

## Troubleshooting
- **Network Error**: Ensure the backend is reachable from the device. Use a tunnel like `ngrok` or `localtunnel` if testing over the internet.
- **Hilt Issues**: Perform a `Project Clean` and `Rebuild` if you encounter dependency injection errors.
