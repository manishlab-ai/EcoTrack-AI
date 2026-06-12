package com.ecotrack.ai.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.core.app.NotificationCompat
import org.tensorflow.lite.support.audio.TensorAudio
import org.tensorflow.lite.task.audio.classifier.AudioClassifier
import kotlin.concurrent.thread

class VoiceWakeService : Service() {

    private val CHANNEL_ID = "VoiceWakeServiceChannel"
    private var isListening = false
    private var audioClassifier: AudioClassifier? = null
    private var tensorAudio: TensorAudio? = null
    private var audioRecord: AudioRecord? = null
    
    // TFLite model - this should be placed in the assets folder
    private val MODEL_FILE = "wake_word_model.tflite"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        initTFLite()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("EcoTrack AI Security")
            .setContentText("Voice Guardian Active. Listening for wake word.")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .build()

        startForeground(1, notification)
        startListening()
        return START_STICKY
    }

    private fun initTFLite() {
        try {
            // Uncomment this when the TFLite model is added to src/main/assets/
            // audioClassifier = AudioClassifier.createFromFile(this, MODEL_FILE)
            // tensorAudio = audioClassifier?.createInputTensorAudio()
            Log.d("VoiceWakeService", "TFLite Model Initialization (Placeholder)")
        } catch (e: Exception) {
            Log.e("VoiceWakeService", "Error loading TFLite model: \${e.message}")
        }
    }

    private fun startListening() {
        if (isListening) return
        isListening = true

        thread(start = true) {
            // Placeholder logic for audio recording and classification
            // Requires RECORD_AUDIO permission to be granted by the user first
            
            /*
            val format = audioClassifier?.requiredTensorAudioFormat
            val bufferSize = AudioRecord.getMinBufferSize(
                format?.sampleRate ?: 16000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )

            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.VOICE_RECOGNITION,
                format?.sampleRate ?: 16000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )

            audioRecord?.startRecording()

            while (isListening) {
                tensorAudio?.load(audioRecord!!)
                val results = audioClassifier?.classify(tensorAudio!!)
                
                results?.forEach { classifications ->
                    classifications.categories.forEach { category ->
                        if (category.label == "where_is_my_phone" && category.score > 0.8f) {
                            Log.d("VoiceWakeService", "WAKE WORD DETECTED!")
                            triggerEmergencyAlarm()
                            isListening = false // Stop listening once found
                            return@thread
                        }
                    }
                }
                Thread.sleep(500)
            }
            */
            
            // Simulating a loop for hackathon demonstration
            while(isListening) {
                Log.d("VoiceWakeService", "Listening... (Simulated)")
                Thread.sleep(5000)
            }
        }
    }

    private fun triggerEmergencyAlarm() {
        Log.d("VoiceWakeService", "Triggering Emergency Recovery Alarm!")
        
        // 1. Override Silent Mode to Max Volume
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(
            AudioManager.STREAM_ALARM,
            audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM),
            0
        )

        // 2. Play Alarm Sound
        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val ringtone = RingtoneManager.getRingtone(applicationContext, alarmUri)
        ringtone.streamType = AudioManager.STREAM_ALARM
        ringtone.play()

        // 3. Vibrate Continuously
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        val pattern = longArrayOf(0, 1000, 500, 1000, 500)
        vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0))

        // 4. Flashlight Strobe
        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            val cameraId = cameraManager.cameraIdList[0]
            thread(start = true) {
                for (i in 0..10) { // Strobe 10 times
                    cameraManager.setTorchMode(cameraId, true)
                    Thread.sleep(200)
                    cameraManager.setTorchMode(cameraId, false)
                    Thread.sleep(200)
                }
            }
        } catch (e: Exception) {
            Log.e("VoiceWakeService", "Camera flashlight error: \${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isListening = false
        audioRecord?.stop()
        audioRecord?.release()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Voice Wake Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
}
