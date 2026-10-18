package com.capstone.tala.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TalaAudioEngine(context: Context) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val soundPool = SfxPlayer(context.applicationContext)
    private var hasAudioFocus = false
    private var isForeground = true
    private var pausedForFocus = false
    private var pausedForBackground = false
    private var repeatIntervalSeconds: Long? = null
    private var repeatJob: Job? = null
    private var releaseFocusJob: Job? = null

    private val focusListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (isForeground && (pausedForFocus || pausedForBackground)) {
                    if (narration.resume()) {
                        pausedForFocus = false
                        pausedForBackground = false
                    }
                }
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                pausedForFocus = narration.pause()
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                pausedForFocus = false
                cancelRepeat()
                narration.stop()
                abandonAudioFocus()
            }
        }
    }

    private val focusRequest: AudioFocusRequest? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            )
            .setOnAudioFocusChangeListener(focusListener)
            .setWillPauseWhenDucked(true)
            .build()
    } else {
        null
    }

    val narration = NarrationManager(context.applicationContext, onSpeechFinished = ::onSpeechFinished)

    fun setLanguage(language: NarrationLanguage) = narration.setLanguage(language)

    fun speak(
        text: String,
        language: NarrationLanguage = NarrationLanguage.ENGLISH,
        character: NarrationCharacter = NarrationCharacter.TALA
    ): Boolean {
        releaseFocusJob?.cancel()
        if (!requestAudioFocus()) return false
        val accepted = narration.speak(text, language, character)
        if (!accepted) abandonAudioFocus()
        return accepted
    }

    fun repeat(): Boolean {
        if (!requestAudioFocus()) return false
        val accepted = narration.repeat()
        if (!accepted) abandonAudioFocus()
        return accepted
    }

    fun enqueue(
        text: String,
        language: NarrationLanguage,
        character: NarrationCharacter
    ): Boolean {
        if (!requestAudioFocus()) return false
        val accepted = narration.enqueue(text, language, character)
        if (!accepted) abandonAudioFocus()
        return accepted
    }

    fun speakKey(
        key: String,
        args: Map<String, String>,
        language: NarrationLanguage,
        character: NarrationCharacter,
        soundOnly: Boolean
    ): Boolean {
        releaseFocusJob?.cancel()
        if (!requestAudioFocus()) return false
        val accepted = narration.speakKey(key, args, language, character, soundOnly)
        if (!accepted) abandonAudioFocus()
        return accepted
    }

    fun enqueueKey(
        key: String,
        args: Map<String, String>,
        language: NarrationLanguage,
        character: NarrationCharacter,
        soundOnly: Boolean
    ): Boolean {
        if (!requestAudioFocus()) return false
        val accepted = narration.enqueueKey(key, args, language, character, soundOnly)
        if (!accepted) abandonAudioFocus()
        return accepted
    }

    fun repeatEvery(seconds: Long) {
        cancelRepeat()
        if (seconds <= 0) return
        repeatIntervalSeconds = seconds
        startRepeatTimer()
    }

    fun cancelRepeat() {
        repeatIntervalSeconds = null
        repeatJob?.cancel()
        repeatJob = null
    }

    fun play(effect: TalaSoundEffect): Boolean {
        releaseFocusJob?.cancel()
        if (!requestAudioFocus()) return false
        if (!soundPool.play(effect)) {
            abandonAudioFocus()
            return false
        }
        releaseFocusJob = scope.launch {
            delay(1500)
            abandonAudioFocus()
        }
        return true
    }

    fun stop() {
        cancelRepeat()
        pausedForBackground = false
        pausedForFocus = false
        narration.stop()
        soundPool.stopAll()
        releaseFocusJob?.cancel()
        abandonAudioFocus()
    }

    fun pauseForBackground() {
        isForeground = false
        repeatJob?.cancel()
        repeatJob = null
        pausedForBackground = narration.pause() || narration.hasPausedUtterance()
        pausedForFocus = false
        soundPool.stopAll()
        releaseFocusJob?.cancel()
        abandonAudioFocus()
    }

    fun resumeFromBackground() {
        isForeground = true
        if (pausedForBackground && requestAudioFocus()) {
            if (narration.resume()) {
                pausedForBackground = false
            } else {
                pausedForBackground = false
                abandonAudioFocus()
            }
        }
        startRepeatTimer()
    }

    fun release() {
        stop()
        soundPool.release()
        narration.shutdown()
        scope.cancel()
    }

    private fun requestAudioFocus(): Boolean {
        if (hasAudioFocus) return true
        val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManager.requestAudioFocus(focusRequest!!)
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(
                focusListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
            )
        }
        hasAudioFocus = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        return hasAudioFocus
    }

    private fun abandonAudioFocus() {
        if (!hasAudioFocus) return
        hasAudioFocus = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManager.abandonAudioFocusRequest(focusRequest!!)
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(focusListener)
        }
    }

    private fun onSpeechFinished() {
        abandonAudioFocus()
    }

    private fun startRepeatTimer() {
        val interval = repeatIntervalSeconds ?: return
        if (repeatJob?.isActive == true) return
        repeatJob = scope.launch {
            while (isActive) {
                delay(interval * 1000)
                if (!pausedForFocus && !pausedForBackground && !narration.isSpeaking.value) repeat()
            }
        }
    }
}