package com.example.engine

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlin.concurrent.thread
import kotlin.math.sin

class AudioManager(context: Context) {
    var isSoundEnabled: Boolean = true
    var isVibrationEnabled: Boolean = true

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    fun vibrate(durationMs: Long = 40, amplitude: Int = 180) {
        if (!isVibrationEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(durationMs, amplitude.coerceIn(1, 255)))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }

    fun playKatanaSlash() {
        if (!isSoundEnabled) return
        vibrate(35, 200)
        playToneSequence(floatArrayOf(440f, 660f, 880f), floatArrayOf(0.04f, 0.04f, 0.05f))
    }

    fun playGunshot() {
        if (!isSoundEnabled) return
        vibrate(45, 240)
        playToneSequence(floatArrayOf(300f, 150f, 80f), floatArrayOf(0.03f, 0.04f, 0.06f))
    }

    fun playJump() {
        if (!isSoundEnabled) return
        vibrate(20, 100)
        playToneSequence(floatArrayOf(250f, 400f), floatArrayOf(0.05f, 0.06f))
    }

    fun playHitImpact() {
        if (!isSoundEnabled) return
        vibrate(60, 255)
        playToneSequence(floatArrayOf(120f, 80f), floatArrayOf(0.06f, 0.08f))
    }

    fun playVictoryBooyah() {
        if (!isSoundEnabled) return
        vibrate(100, 255)
        playToneSequence(
            floatArrayOf(523.25f, 659.25f, 783.99f, 1046.50f),
            floatArrayOf(0.12f, 0.12f, 0.15f, 0.35f)
        )
    }

    private fun playToneSequence(frequencies: FloatArray, durations: FloatArray) {
        thread(start = true, isDaemon = true) {
            try {
                val sampleRate = 22050
                var totalSamples = 0
                for (d in durations) totalSamples += (sampleRate * d).toInt()

                val buffer = ShortArray(totalSamples)
                var offset = 0
                for (i in frequencies.indices) {
                    val freq = frequencies[i]
                    val durationSamples = (sampleRate * durations[i]).toInt()
                    for (j in 0 until durationSamples) {
                        val angle = 2.0 * Math.PI * j * freq / sampleRate
                        val envelope = (1.0 - j.toDouble() / durationSamples).toFloat() // decay
                        val sample = (sin(angle) * Short.MAX_VALUE * 0.45f * envelope).toInt()
                        if (offset + j < buffer.size) {
                            buffer[offset + j] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                        }
                    }
                    offset += durationSamples
                }

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()
                Thread.sleep((durations.sum() * 1000).toLong() + 50)
                audioTrack.release()
            } catch (_: Exception) {}
        }
    }
}
