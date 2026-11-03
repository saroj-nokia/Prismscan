# PrismScan — Native Android QR & Barcode Scanner

**PrismScan** is a high-performance, 100% offline native Android QR and barcode scanner built with Kotlin and Jetpack Compose. Built with Material 3 Expressive design, physics-driven spring animations, and zero-trust URL security checks.

---

## Features

### 📸 High-Speed Offline Scanning
- **CameraX + ML Kit:** Real-time preview and image analysis using Google's bundled on-device model (`com.google.mlkit:barcode-scanning`). Works completely offline without requiring Google Play Services model downloads.
- **Broad Format Support:** QR Code, Aztec, Data Matrix, PDF417, Code 128, Code 39, Code 93, Codabar, EAN-13, EAN-8, ITF, UPC-A, UPC-E.
- **Interactive Camera Controls:**
  - Flashlight / Torch toggle with illuminated state indicator
  - Pinch-to-zoom (up to 8x) and zoom ratio quick selector (1x, 2x, 4x)
  - Tap-to-focus with `FocusMeteringAction`
  - Haptic vibration feedback following system settings
  - Animated laser scan line and responsive corner viewfinder brackets
- **Zero-Permission Gallery Scanner:** Pick any image from your gallery using the Android Photo Picker (`ActivityResultContracts.PickVisualMedia`) without requiring broad storage permissions.
- **Graceful Permission Handling:** Informative rationale cards with a direct deep link to application settings if permissions were previously denied.

### 🛡️ URL Safety & Phishing Protection
- **Never Auto-Opens:** Prevents malicious redirects by never automatically opening scanned URLs.
- **Destination Domain Highlight:** Isolates and prominently displays the domain in large monospace type to expose deceptive path tricks.
- **Insecure HTTP Warnings:** Flags unencrypted HTTP connections with amber warning badges.
- **Punycode / Homoglyph Defense:** Detects internationalized domain names (`xn--`), raw numerical IP address hosts, and deceptive characters often used in phishing attacks.
- **SplitButton Action:** Clear "Open Link" button with an instant "Copy URL" option.

### 📋 Rich Content Actions
- **Wi-Fi Networks:** Decodes SSID, password, and security encryption (WPA/WEP/Open). Quick button to copy password and jump to Wi-Fi settings.
- **Contacts (vCard):** Parses name, phone numbers, emails, organization, and job title with direct "Add to Contacts" intent.
- **Phone Numbers & SMS:** Direct one-tap "Dial" and "Send SMS" actions.
- **Email:** Parses recipient, subject, and body with "Send Email" action.
- **Geographic Coordinates:** Latitude and longitude mapping with direct navigation on Google Maps.
- **Calendar Events:** Summaries, descriptions, start/end timestamps, and locations with direct "Add to Calendar" intent.
- **Product Barcodes:** Quick lookup on Google for EAN/UPC retail codes.
- **Text:** Full copy and system share sheet integration.

### 💾 Local History (Room Database)
- **Persisted Scans:** All scans are saved to a local SQLite database via Room with reactive `Flow` updates.
- **Instant Search:** Filter scan history in real time by payload text or format name.
- **Filter Chips:** Filter by content type (`All`, `URL`, `Wi-Fi`, `Contact`, `Phone`, `SMS`, `Email`, `Location`, `Calendar`, `Product Barcode`, `Text`).
- **Swipe-to-Delete & Clear All:** Swipe items left or right to dismiss, or clear all history with a confirmation dialog.
- **Favorites:** Star frequently used codes.
- **Privacy Setting:** Option to turn history saving completely off.

### 🎨 Material 3 Expressive Design
- **MaterialExpressiveTheme:** Powered by `MotionScheme.expressive()` for natural, physics-driven spring transitions.
- **Dynamic Color (Material You):** Adapts to system wallpaper colors on Android 12+, with a vibrant Electric Indigo and Cyan fallback palette.
- **Shape Morphing:** Floating scan button morphs between idle pill, scanning squircle, and success circle states.
- **Adaptive Navigation Suite:** Responsive bottom navigation bar on handheld phones, automatically transforming into a Navigation Rail on tablets and foldables.
- **Adaptive Layout:** Automatic List-Detail pane for history on large screens (tablets and Chromebooks).
- **Monochrome Themed Icon:** Supports Android 13+ themed dynamic home screen launcher icons.

---

## Tech Stack & Architecture

- **Language:** 100% Kotlin
- **UI Framework:** Jetpack Compose with Material 3 Expressive (`androidx.compose.material3:1.5.0-alpha10`)
- **Architecture:** Clean MVVM (Model-View-ViewModel) with Kotlin Coroutines and `StateFlow`
- **Scanning:** CameraX (`1.5.0`) + ML Kit Barcode Scanning (`17.3.0`)
- **Database:** Android Jetpack Room (`2.7.0`) with KSP code generation
- **Adaptive:** `androidx.compose.material3:material3-adaptive-navigation-suite`
- **Permissions:** Zero storage permissions (Photo Picker), `android.permission.CAMERA`, and `android.permission.VIBRATE`

---

## Building and Running

### Prerequisites
- JDK 17+
- Android SDK 36 (Android 16 / 17 preview)
- Gradle 8.11+ / AGP 9.1.1

### Build APK
```bash
gradle :app:assembleDebug
```

### Run Unit & Robolectric Tests
```bash
gradle :app:testDebugUnitTest
```
