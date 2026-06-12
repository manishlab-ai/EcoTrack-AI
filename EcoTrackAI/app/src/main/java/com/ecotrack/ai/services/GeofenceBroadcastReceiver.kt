package com.ecotrack.ai.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent == null || geofencingEvent.hasError()) {
            Log.e("GeofenceReceiver", "Geofencing error: \${geofencingEvent?.errorCode}")
            return
        }

        val geofenceTransition = geofencingEvent.geofenceTransition

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            val triggeringGeofences = geofencingEvent.triggeringGeofences
            val zoneId = triggeringGeofences?.firstOrNull()?.requestId ?: "Unknown Zone"

            Log.w("GeofenceReceiver", "User exited Safe Zone: \$zoneId")
            
            // Trigger "Left Phone Behind" warning or arm the Anti-Theft system automatically
            triggerLeftBehindWarning(context, zoneId)
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Log.d("GeofenceReceiver", "Entered Safe Zone. Disarming strict anti-theft.")
        }
    }

    private fun triggerLeftBehindWarning(context: Context, zone: String) {
        // Send a high priority notification or SMS
        // e.g. "You appear to have moved away from your device at \$zone"
        Log.d("GeofenceReceiver", "WARNING: You left your device at \$zone!")
        
        // Arm theft detection automatically since it's no longer in a safe zone
        val intent = Intent(context, TheftDetectionService::class.java).apply {
            action = "ARM_SYSTEM"
        }
        context.startService(intent)
    }
}
