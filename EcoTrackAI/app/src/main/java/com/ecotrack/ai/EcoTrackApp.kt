package com.ecotrack.ai

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class EcoTrackApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize components
    }
}
