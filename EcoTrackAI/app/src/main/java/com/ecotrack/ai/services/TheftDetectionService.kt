package com.ecotrack.ai.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.util.Log
import kotlin.math.sqrt

class TheftDetectionService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    
    // Theft variables
    private var isArmed = false
    private var baselineAcceleration = 9.8f
    private val MOVEMENT_THRESHOLD = 2.5f // Threshold for sudden movement
    
    private var riskScore = 0

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == "ARM_SYSTEM") {
            armSystem()
        } else if (action == "DISARM_SYSTEM") {
            disarmSystem()
        }
        return START_STICKY
    }

    private fun armSystem() {
        if (!isArmed) {
            isArmed = true
            riskScore = 0
            accelerometer?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            }
            Log.d("TheftDetection", "Anti-Theft System Armed.")
        }
    }

    private fun disarmSystem() {
        if (isArmed) {
            isArmed = false
            riskScore = 0
            sensorManager.unregisterListener(this)
            Log.d("TheftDetection", "Anti-Theft System Disarmed.")
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (!isArmed || event == null) return

        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            // Calculate overall acceleration
            val currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta = Math.abs(currentAcceleration - baselineAcceleration)

            if (delta > MOVEMENT_THRESHOLD) {
                Log.d("TheftDetection", "Sudden movement detected! Delta: \$delta")
                increaseRiskScore(15)
            }
        }
    }

    private fun increaseRiskScore(amount: Int) {
        riskScore += amount
        Log.w("TheftDetection", "Risk score increased to \$riskScore")
        
        when {
            riskScore >= 100 -> triggerCriticalTheftAlert()
            riskScore >= 70 -> Log.w("TheftDetection", "High Risk! Possible theft in progress.")
            riskScore >= 40 -> Log.w("TheftDetection", "Medium Risk. Monitoring closely.")
        }
    }

    private fun triggerCriticalTheftAlert() {
        Log.e("TheftDetection", "CRITICAL THEFT ALERT! Locking device and sending location.")
        // Here we would integrate CameraX to take a silent photo
        // and fetch GPS to send via SMS.
        
        // Reset to prevent spamming
        riskScore = 0 
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        disarmSystem()
    }
}
