# EcoTrack AI - Autonomous Device Recovery & Security Agent

EcoTrack AI is a production-grade Android application designed to locate, protect, and recover smartphones using on-device AI, proximity sensing, and offline automation.

Built for the **FAR AWAY Hackathon**.

## Core Features
1. **Offline Voice Wake Detection**: Uses TFLite to listen for a wake phrase even offline, bypassing silent mode to trigger maximum volume alarms and strobe lights.
2. **Intelligent Proximity Finder**: Utilizes BLE RSSI to calculate distance to the phone, offering a Hot/Cold radar UI.
3. **Last Breath SOS**: BroadcastReceiver automatically blasts a high-accuracy GPS link via SMS to emergency contacts when battery drops to 1%.
4. **Theft Detection**: Monitors sudden accelerometer changes to track risk scores.
5. **Safe Zone Intelligence**: Google Play Geofencing to detect when you leave your device behind.

## Tech Stack
- **Kotlin & Jetpack Compose**
- **MVVM + Clean Architecture**
- **Dagger Hilt**
- **TensorFlow Lite (Audio Classification)**
- **Android Foreground Services & BroadcastReceivers**

## Setup
1. Open the `EcoTrackAI` folder in Android Studio.
2. Allow Gradle to sync.
3. Ensure you have a physical device to test native hardware features (BLE, SMS, AudioRecord).
4. Run the app!
