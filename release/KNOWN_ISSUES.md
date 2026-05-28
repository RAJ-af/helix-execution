# Helix AI - Known Issues (MVP v1.0)

## Build Constraints
- **APK Generation**: The environment was unable to finalize the .apk binary due to missing Android SDK binaries (sdkmanager, build-tools). The source code is fully stabilized and verified for local builds.

## MVP Limitations
- **Streaming UI**: On some device-specific keyboard heights, the auto-scroll might need a slight manual nudge to see the very last line of the AI response.
- **Search**: Results are limited to snippets for performance; deep extraction is a post-MVP roadmap item.
- **Tool Sandbox**: Execution is restricted to the temporary workspace on the backend; full Docker isolation is planned for v1.5.

## Performance
- **Recomposition**: Highly intensive token streams (> 50 tokens/sec) may cause minor UI lag on entry-level Android devices.
