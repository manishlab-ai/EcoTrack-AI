package com.ecotrack.ai.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.telephony.SmsManager
import android.util.Log
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

class BatteryReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val batteryPct = level * 100 / scale.toFloat()

            Log.d("BatteryReceiver", "Current Battery Level: \$batteryPct%")

            // Trigger at 1% for "Last Breath SOS"
            // Using 15% for testing purposes during development, revert to 1.0f for prod
            if (batteryPct <= 1.0f) {
                triggerLastBreathSOS(context)
            }
        }
    }

    private fun triggerLastBreathSOS(context: Context) {
        Log.d("BatteryReceiver", "CRITICAL BATTERY! Triggering Last Breath SOS.")

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        
        try {
            // Note: In a real scenario, check if permissions are granted before requesting
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val mapsLink = "https://maps.google.com/?q=\${location.latitude},\${location.longitude}"
                        val timestamp = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
                        
                        val message = "EcoTrack AI Alert:\n" +
                                "Device battery critically low (1%).\n" +
                                "Last known location:\n\$mapsLink\n" +
                                "Timestamp: \$timestamp"

                        sendEmergencySMS(context, message)
                    } else {
                        Log.e("BatteryReceiver", "Location is null. Cannot send precise location.")
                    }
                }
                .addOnFailureListener {
                    Log.e("BatteryReceiver", "Failed to get location for SOS: \${it.message}")
                }
        } catch (e: SecurityException) {
            Log.e("BatteryReceiver", "Location permission missing for SOS: \${e.message}")
        }
    }

    private fun sendEmergencySMS(context: Context, message: String) {
        // In a real application, fetch the emergency contacts from local storage (e.g. Room Database or DataStore)
        val emergencyContact = "+1234567890" // Placeholder
        
        try {
            val smsManager: SmsManager = context.getSystemService(SmsManager::class.java)
            smsManager.sendTextMessage(emergencyContact, null, message, null, null)
            Log.d("BatteryReceiver", "Last Breath SOS SMS sent successfully to \$emergencyContact")
        } catch (e: Exception) {
            Log.e("BatteryReceiver", "Failed to send SMS: \${e.message}")
        }
    }
}
