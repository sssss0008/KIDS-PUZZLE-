package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.speech.tts.TextToSpeech
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.sin

class KidsSoundManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private val scope = CoroutineScope(Dispatchers.Default)

    var isSpeechEnabled: Boolean = true
    var isSoundEffectsEnabled: Boolean = true
    var speechRate: Float = 0.88f
    var speechPitch: Float = 1.2f

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e("KidsSoundManager", "Error initializing TTS", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.getDefault())
            }
            tts?.setPitch(speechPitch)
            tts?.setSpeechRate(speechRate)
            isTtsReady = true
        } else {
            isTtsReady = false
        }
    }

    fun speak(text: String, queueMode: Int = TextToSpeech.QUEUE_FLUSH) {
        if (!isSpeechEnabled || !isTtsReady) return
        try {
            tts?.setPitch(speechPitch)
            tts?.setSpeechRate(speechRate)
            tts?.speak(text, queueMode, null, "kids_tts_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            Log.e("KidsSoundManager", "TTS speak failed", e)
        }
    }

    fun stopSpeaking() {
        try {
            tts?.stop()
        } catch (_: Exception) {}
    }

    // Synthesized Sound Effects
    fun playPopSound() {
        if (!isSoundEffectsEnabled) return
        scope.launch {
            playToneSweep(startFreq = 400.0, endFreq = 950.0, durationMs = 80)
        }
    }

    fun playCorrectChime() {
        if (!isSoundEffectsEnabled) return
        scope.launch {
            playArpeggio(listOf(523.25, 659.25, 783.99, 1046.50), noteDurationMs = 60)
        }
    }

    fun playWrongBuzz() {
        if (!isSoundEffectsEnabled) return
        scope.launch {
            playToneSweep(startFreq = 220.0, endFreq = 160.0, durationMs = 180)
        }
    }

    fun playCardFlip() {
        if (!isSoundEffectsEnabled) return
        scope.launch {
            playToneSweep(startFreq = 600.0, endFreq = 850.0, durationMs = 45)
        }
    }

    fun playCelebrationFanfare() {
        if (!isSoundEffectsEnabled) return
        scope.launch {
            playArpeggio(listOf(523.25, 659.25, 783.99, 1046.50, 1318.51), noteDurationMs = 90)
        }
    }

    fun playStarCollect() {
        if (!isSoundEffectsEnabled) return
        scope.launch {
            playToneSweep(startFreq = 880.0, endFreq = 1760.0, durationMs = 100)
        }
    }

    private fun playToneSweep(startFreq: Double, endFreq: Double, durationMs: Int) {
        val sampleRate = 22050
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val generatedSnd = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val currentFreq = startFreq + (endFreq - startFreq) * progress
            val angle = 2.0 * Math.PI * i / (sampleRate / currentFreq)
            // Envelope to avoid clicking
            val envelope = (1.0 - progress) * (sin(progress * Math.PI))
            generatedSnd[i] = (sin(angle) * 32767 * envelope * 0.45).toInt().toShort()
        }

        playPcmData(generatedSnd, sampleRate)
    }

    private fun playArpeggio(freqs: List<Double>, noteDurationMs: Int) {
        val sampleRate = 22050
        val samplesPerNote = (sampleRate * (noteDurationMs / 1000.0)).toInt()
        val totalSamples = samplesPerNote * freqs.size
        val generatedSnd = ShortArray(totalSamples)

        var offset = 0
        for (freq in freqs) {
            for (i in 0 until samplesPerNote) {
                val progress = i.toDouble() / samplesPerNote
                val angle = 2.0 * Math.PI * i / (sampleRate / freq)
                val envelope = sin(progress * Math.PI)
                generatedSnd[offset + i] = (sin(angle) * 32767 * envelope * 0.4).toInt().toShort()
            }
            offset += samplesPerNote
        }

        playPcmData(generatedSnd, sampleRate)
    }

    private fun playPcmData(pcmData: ShortArray, sampleRate: Int) {
        var audioTrack: AudioTrack? = null
        try {
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSize = maxOf(minBufferSize, pcmData.size * 2)

            audioTrack = AudioTrack.Builder()
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
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(pcmData, 0, pcmData.size)
            audioTrack.play()

            // Sleep briefly to let sound finish, then release
            Thread.sleep((pcmData.size.toDouble() / sampleRate * 1000).toLong() + 20)
        } catch (_: Exception) {
        } finally {
            try {
                audioTrack?.stop()
                audioTrack?.release()
            } catch (_: Exception) {}
        }
    }

    fun release() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (_: Exception) {}
    }
}
