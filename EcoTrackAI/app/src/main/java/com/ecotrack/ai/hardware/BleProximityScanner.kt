package com.ecotrack.ai.hardware

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.pow

class BleProximityScanner(private val context: Context) {

    private val bluetoothManager: BluetoothManager = context.getSystemService(BluetoothManager::class.java)
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    private val bluetoothLeScanner: BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner

    private val _proximityStatus = MutableStateFlow<ProximityState>(ProximityState.Unknown)
    val proximityStatus: StateFlow<ProximityState> = _proximityStatus

    private var isScanning = false

    // Target device MAC address (in a real app, this would be paired/exchanged securely)
    private val TARGET_DEVICE_MAC = "00:00:00:00:00:00"

    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            
            val device = result.device
            // If we found our target device (or just any device for hackathon demo purposes)
            if (device.address == TARGET_DEVICE_MAC || TARGET_DEVICE_MAC == "00:00:00:00:00:00") {
                val rssi = result.rssi
                val distance = calculateDistance(rssi, result.txPower)
                
                Log.d("BleScanner", "Found device \${device.address} at RSSI: \$rssi, Est. Distance: \$distance meters")
                
                updateProximityState(distance)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e("BleScanner", "Scan failed with error: \$errorCode")
            super.onScanFailed(errorCode)
        }
    }

    @SuppressLint("MissingPermission")
    fun startScanning() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            Log.e("BleScanner", "Bluetooth is disabled or not supported.")
            return
        }

        if (!isScanning) {
            bluetoothLeScanner?.startScan(scanCallback)
            isScanning = true
            Log.d("BleScanner", "BLE Scanning started.")
        }
    }

    @SuppressLint("MissingPermission")
    fun stopScanning() {
        if (isScanning) {
            bluetoothLeScanner?.stopScan(scanCallback)
            isScanning = false
            Log.d("BleScanner", "BLE Scanning stopped.")
            _proximityStatus.value = ProximityState.Unknown
        }
    }

    /**
     * Calculates an approximate distance in meters based on RSSI and TxPower.
     */
    private fun calculateDistance(rssi: Int, txPower: Int): Double {
        val defaultTxPower = -59 // Default if txPower is not provided
        val actualTxPower = if (txPower == ScanResult.TX_POWER_NOT_PRESENT) defaultTxPower else txPower
        
        if (rssi == 0) return -1.0 // Cannot determine distance

        val ratio = rssi * 1.0 / actualTxPower
        return if (ratio < 1.0) {
            ratio.pow(10.0)
        } else {
            val accuracy = (0.89976) * ratio.pow(7.7095) + 0.111
            accuracy
        }
    }

    private fun updateProximityState(distanceMeters: Double) {
        val state = when {
            distanceMeters < 0 -> ProximityState.Unknown
            distanceMeters <= 1.0 -> ProximityState.Found
            distanceMeters <= 3.0 -> ProximityState.VeryClose
            distanceMeters <= 8.0 -> ProximityState.Hot
            distanceMeters <= 15.0 -> ProximityState.Warm
            else -> ProximityState.Cold
        }
        _proximityStatus.value = state
    }
}

sealed class ProximityState {
    object Unknown : ProximityState()
    object Cold : ProximityState()
    object Warm : ProximityState()
    object Hot : ProximityState()
    object VeryClose : ProximityState()
    object Found : ProximityState()
}
