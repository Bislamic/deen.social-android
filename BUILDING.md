# Building the deen.social Android client locally

This document covers building a debug APK on your machine to verify the
Bislamic patches before publishing. For Google Play release builds, see
the upstream Mastodon Android documentation.

## Prerequisites

- **JDK 21** (Temurin/Adoptium recommended)
- **Android SDK** with platforms 36, build-tools 36.0.0, platform-tools
- **Git Bash or WSL** on Windows (the Gradle wrapper is a POSIX shell script)

Verify:

```bash
java -version    # must show "21.x.x"
echo $ANDROID_HOME
```

If `ANDROID_HOME` is empty, set it once and persist it:

- **Windows (PowerShell, persistent):**
  ```powershell
  setx ANDROID_HOME "$env:LOCALAPPDATA\Android\Sdk"
  ```
- **macOS/Linux (~/.bashrc or ~/.zshrc):**
  ```bash
  export ANDROID_HOME="$HOME/Android/Sdk"
  ```

## One-time setup

Create `local.properties` at the repo root pointing at your SDK:

```properties
# local.properties — not committed (in .gitignore)
sdk.dir=C:/Users/YOU/AppData/Local/Android/Sdk
```

**Use forward slashes** even on Windows; Java's properties parser interprets
backslashes as escape sequences and the build will fail with the cryptic
"El nombre de archivo no es correcto" / "filename not correct" error.

## Build a debug APK

```bash
./gradlew :mastodon:assembleDebug
```

First run downloads ~500 MB of dependencies and takes 5-10 minutes.
Subsequent builds are 30-90 seconds.

The APK lands at:

```
mastodon/build/outputs/apk/debug/mastodon-debug.apk
```

## Install on emulator or device

```bash
# Start an emulator from Android Studio first, then:
$ANDROID_HOME/platform-tools/adb install -r mastodon/build/outputs/apk/debug/mastodon-debug.apk
```

Or drag-drop the APK onto a running emulator window.

## What to verify after install

The Bislamic patches are deliberately invasive in the auth/onboarding flow.
After install, you should see:

1. **Splash screen** — buttons say "Get Started" and "Log In".
2. **Tap "Get Started"** — does **NOT** open the instance picker. Instead:
   loads deen.social server info → opens the instance rules screen.
3. **Tap "Log In"** — does **NOT** open the picker. Instead: loads
   deen.social and starts OAuth in the system browser.
4. **App launcher icon name** — reads "deen.social", not "Mastodon".
5. **Settings → About** — version line says "deen.social for Android v…",
   not "Mastodon for Android v…".
6. **Welcome screen text** — heading says "Welcome to deen.social"; body
   describes the deen.social Muslim community, not the wider Mastodon
   network's pick-a-server framing.

If any of these still says "Mastodon" or shows the picker, that's a bug in
our patches — please open an issue with the screenshot.

## Things that do NOT work in a debug build

- **Push notifications** — require Firebase Cloud Messaging credentials
  (google-services.json) which we don't ship. Online-only experience.
- **Google Maps location pickers** — needs an API key in `secrets.properties`.
- **Crowdin / translation downloads** — needs API tokens.
- **App signing for release** — needs a production keystore.

These are all release-build concerns and don't block functional verification
of the auth and branding patches.

## Common errors

| Error | Cause | Fix |
|---|---|---|
| `SDK location not found` | `local.properties` missing or malformed | Recreate with forward slashes |
| `Could not determine ... compileDebugJavaWithJavac` | Path escape issue | Use forward slashes in `local.properties` |
| `compileSdk 36 not installed` | SDK platform missing | Open Android Studio → SDK Manager → install platform 36 |
| `Unsupported class file major version` | Wrong JDK | Switch to JDK 21 (`java -version`) |
| `OutOfMemoryError` during build | Default JVM heap too small | Add `org.gradle.jvmargs=-Xmx4g` to `gradle.properties` |
