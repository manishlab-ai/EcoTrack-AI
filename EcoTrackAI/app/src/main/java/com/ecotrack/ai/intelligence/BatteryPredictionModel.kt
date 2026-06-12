package com.ecotrack.ai.intelligence

/**
 * A lightweight heuristic model for the hackathon to predict battery drain.
 * In a production app, this would use TFLite to run a regression model 
 * based on app usage history, network state, and screen brightness.
 */
class BatteryPredictionModel {

    // Assumed average drain rate (percent per minute) under normal usage
    private val averageDrainRatePerMin = 0.5

    /**
     * Estimates remaining time in minutes until shutdown (1%)
     */
    fun predictTimeUntilShutdown(currentBatteryPercent: Int, isPowerSaving: Boolean): Int {
        val targetPercent = 1 // Last breath SOS triggers at 1%
        if (currentBatteryPercent <= targetPercent) return 0

        val percentToDrain = currentBatteryPercent - targetPercent
        
        var effectiveDrainRate = averageDrainRatePerMin
        if (isPowerSaving) {
            effectiveDrainRate *= 0.6 // 40% slower drain on power saver
        }

        val minutesRemaining = (percentToDrain / effectiveDrainRate).toInt()
        return minutesRemaining
    }
    
    fun formatPrediction(minutes: Int): String {
        val hours = minutes / 60
        val mins = minutes % 60
        return if (hours > 0) {
            "\${hours}h \${mins}m remaining"
        } else {
            "\${mins}m remaining"
        }
    }
}
