package com.ecotrack.ai.intelligence

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.ecotrack.ai.services.GeofenceBroadcastReceiver
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

class SafeZoneManager(private val context: Context) {

    private val geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(context)
    private val geofenceList = mutableListOf<Geofence>()

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    fun addSafeZone(id: String, latitude: Double, longitude: Double, radiusMeters: Float = 50f) {
        val geofence = Geofence.Builder()
            .setRequestId(id)
            .setCircularRegion(latitude, longitude, radiusMeters)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()
            
        geofenceList.add(geofence)
        Log.d("SafeZoneManager", "Added Safe Zone: \$id")
    }

    @SuppressLint("MissingPermission")
    fun startGeofencing() {
        if (geofenceList.isEmpty()) {
            Log.w("SafeZoneManager", "No safe zones to monitor.")
            return
        }

        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofences(geofenceList)
            .build()

        geofencingClient.addGeofences(request, geofencePendingIntent).run {
            addOnSuccessListener {
                Log.d("SafeZoneManager", "Geofences successfully added.")
            }
            addOnFailureListener {
                Log.e("SafeZoneManager", "Failed to add geofences: \${it.message}")
            }
        }
    }

    fun stopGeofencing() {
        geofencingClient.removeGeofences(geofencePendingIntent).run {
            addOnSuccessListener {
                Log.d("SafeZoneManager", "Geofences removed.")
            }
        }
    }
}
