package com.capstone.tala.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

enum class NarrationAvailability {
    INITIALIZING,
    REQUESTED_LANGUAGE,
    ENGLISH_FALLBACK,
    UNAVAILABLE
}

class NarrationManager(
    private val context: Context,
    initialLanguage: NarrationLanguage = NarrationLanguage.ENGLISH,
    private val onSpeechFinished: () -> Unit = {}
) : TextToSpeech.OnInitListener {

    private data class Request(
        val text: String,
        val subtitle: String,
        val language: NarrationLanguage,
        val character: NarrationCharacter,
        val clipPath: String? = null
    )

    private val catalog = NarrationAssetCatalog(context)
    private var tts: TextToSpeech? = null
    private var activeMediaPlayer: MediaPlayer? = null
    private var currentLanguage = initialLanguage
    private var initialized = false
    private var speakingEnabled = true
    private var pendingRequest: Request? = null
    private val queuedRequests = ArrayDeque<Request>()
    private var lastRequest: Request? = null
    private var activeRequest: Request? = null
    private var pausedRequest: Request? = null
    private var activeUtteranceId: String? = null

    private val mutableAvailability = MutableStateFlow(NarrationAvailability.INITIALIZING)
    val availability: StateFlow<NarrationAvailability> = mutableAvailability.asStateFlow()

    private val mutableIsSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = mutableIsSpeaking.asStateFlow()

    private val mutableSubtitle = MutableStateFlow("")
    val subtitle: StateFlow<String> = mutableSubtitle.asStateFlow()

    private val progressListener = object : UtteranceProgressListener() {
        override fun onStart(utteranceId: String?) {
            if (utteranceId == activeUtteranceId) mutableIsSpeaking.value = true
        }

        override fun onDone(utteranceId: String?) = finishUtterance(utteranceId)

        @Deprecated("Deprecated in Java")
        @Suppress("DEPRECATION")
        override fun onError(utteranceId: String?) = finishUtterance(utteranceId)
    }

    init {
        tts = TextToSpeech(context.applicationContext, this)
        tts?.setOnUtteranceProgressListener(progressListener)
    }

    @Deprecated("Use TextToSpeech.OnInitListener callback for compatibility with the supported TTS API.")
    @Suppress("DEPRECATION")
    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) {
            mutableAvailability.value = NarrationAvailability.UNAVAILABLE
            pendingRequest = null
            queuedRequests.clear()
            onSpeechFinished()
            return
        }

        initialized = true
        applyLanguage(currentLanguage)
        pendingRequest?.also {
            pendingRequest = null
            startRequest(it)
        }
    }

    fun setLanguage(language: NarrationLanguage) {
        currentLanguage = language
        if (initialized) applyLanguage(language)
    }

    private fun applyLanguage(language: NarrationLanguage): Boolean {
        val engine = tts ?: return false
        val resolution = NarrationLanguageResolver.resolve(language) { locale ->
            engine.isLanguageAvailable(locale) >= TextToSpeech.LANG_AVAILABLE
        }
        if (!resolution.available) {
            mutableAvailability.value = NarrationAvailability.UNAVAILABLE
            return false
        }

        val result = engine.setLanguage(resolution.locale)
        if (result < TextToSpeech.LANG_AVAILABLE) {
            mutableAvailability.value = NarrationAvailability.UNAVAILABLE
            return false
        }

        mutableAvailability.value = if (resolution.usingFallback) {
            NarrationAvailability.ENGLISH_FALLBACK
        } else {
            NarrationAvailability.REQUESTED_LANGUAGE
        }
        return true
    }

    fun speak(
        text: String,
        language: NarrationLanguage = currentLanguage,
        character: NarrationCharacter = NarrationCharacter.TALA
    ): Boolean {
        if (text.isBlank() || !speakingEnabled) return false
        queuedRequests.clear()
        pendingRequest = null
        pausedRequest = null
        activeRequest = null
        activeUtteranceId = null
        tts?.stop()
        mutableIsSpeaking.value = false
        return startRequest(Request(text, text, language, character))
    }

    fun speakKey(
        key: String,
        args: Map<String, String> = emptyMap(),
        language: NarrationLanguage = currentLanguage,
        character: NarrationCharacter = NarrationCharacter.TALA,
        soundOnly: Boolean = false
    ): Boolean {
        val request = resolveKey(key, args, language, character, soundOnly) ?: return false
        queuedRequests.clear()
        pendingRequest = null
        pausedRequest = null
        stopCurrentPlayback()
        return startRequest(request)
    }

    fun enqueue(
        text: String,
        language: NarrationLanguage = currentLanguage,
        character: NarrationCharacter = NarrationCharacter.TALA
    ): Boolean {
        if (text.isBlank() || !speakingEnabled) return false
        val request = Request(text, text, language, character)
        if (activeUtteranceId != null || pausedRequest != null || pendingRequest != null) {
            queuedRequests.addLast(request)
            return true
        }
        return startRequest(request)
    }

    fun enqueueKey(
        key: String,
        args: Map<String, String> = emptyMap(),
        language: NarrationLanguage = currentLanguage,
        character: NarrationCharacter = NarrationCharacter.TALA,
        soundOnly: Boolean = false
    ): Boolean {
        val request = resolveKey(key, args, language, character, soundOnly) ?: return false
        if (activeUtteranceId != null || pausedRequest != null || pendingRequest != null) {
            queuedRequests.addLast(request)
            return true
        }
        return startRequest(request)
    }

    private fun startRequest(request: Request): Boolean {
        currentLanguage = request.language
        lastRequest = request
        mutableSubtitle.value = request.subtitle

        activeUtteranceId = null
        activeRequest = null
        stopCurrentPlayback()
        tts?.stop()
        val utteranceId = "tala_${System.nanoTime()}"
        activeUtteranceId = utteranceId
        activeRequest = request
        pausedRequest = null

        if (request.clipPath != null) return playRecordedClip(request, utteranceId)

        if (!initialized) {
            pendingRequest = request
            return true
        }
        if (!applyLanguage(request.language)) {
            activeUtteranceId = null
            activeRequest = null
            mutableIsSpeaking.value = false
            onSpeechFinished()
            return false
        }

        val engine = tts ?: return false
        engine.setPitch(request.character.pitch)
        engine.setSpeechRate(request.character.speechRate)
        mutableIsSpeaking.value = true
        val result = engine.speak(
            request.text,
            TextToSpeech.QUEUE_FLUSH,
            Bundle(),
            utteranceId
        )
        if (result == TextToSpeech.ERROR) {
            finishUtterance(utteranceId)
            return false
        }
        return true
    }

    fun repeat(): Boolean {
        val request = lastRequest ?: return false
        return startRequest(request)
    }

    fun pause(): Boolean {
        val request = activeRequest ?: pendingRequest ?: return false
        pausedRequest = request
        pendingRequest = null
        activeUtteranceId = null
        activeRequest = null
        stopCurrentPlayback()
        tts?.stop()
        mutableIsSpeaking.value = false
        return true
    }

    fun hasPausedUtterance(): Boolean = pausedRequest != null

    fun resume(): Boolean {
        val request = pausedRequest ?: return false
        pausedRequest = null
        return speak(request.text, request.language, request.character)
    }

    fun stop() {
        activeUtteranceId = null
        activeRequest = null
        pendingRequest = null
        queuedRequests.clear()
        pausedRequest = null
        lastRequest = null
        stopCurrentPlayback()
        tts?.stop()
        mutableIsSpeaking.value = false
        mutableSubtitle.value = ""
    }

    fun setSpeakingEnabled(enabled: Boolean) {
        speakingEnabled = enabled
        if (!enabled) stop()
    }

    fun shutdown() {
        stop()
        tts?.shutdown()
        tts = null
        initialized = false
    }

    private fun finishUtterance(utteranceId: String?) {
        if (utteranceId == null || utteranceId != activeUtteranceId) return
        activeUtteranceId = null
        activeRequest = null
        stopCurrentPlayback()
        mutableIsSpeaking.value = false
        while (queuedRequests.isNotEmpty()) {
            if (startRequest(queuedRequests.removeFirst())) return
        }
        mutableSubtitle.value = ""
        onSpeechFinished()
    }

    private fun resolveKey(
        key: String,
        args: Map<String, String>,
        language: NarrationLanguage,
        character: NarrationCharacter,
        soundOnly: Boolean
    ): Request? {
        val resolution = catalog.resolve(key, args, language, soundOnly) ?: return null
        return when (resolution) {
            is NarrationResolution.RecordedClip -> Request(
                text = resolution.fallbackText,
                subtitle = resolution.subtitle,
                language = language,
                character = character,
                clipPath = resolution.assetPath
            )
            is NarrationResolution.TextToSpeech -> Request(
                text = resolution.text,
                subtitle = resolution.subtitle,
                language = language,
                character = character
            )
        }
    }

    private fun playRecordedClip(request: Request, utteranceId: String): Boolean {
        val player = MediaPlayer()
        return runCatching {
            player.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            )
            context.assets.openFd(request.clipPath!!).use { descriptor ->
                player.setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
            }
            player.setOnCompletionListener { completedPlayer ->
                if (activeMediaPlayer === completedPlayer) {
                    activeMediaPlayer = null
                    completedPlayer.release()
                    finishUtterance(utteranceId)
                }
            }
            player.setOnErrorListener { failedPlayer, _, _ ->
                if (activeMediaPlayer === failedPlayer) {
                    activeMediaPlayer = null
                    failedPlayer.release()
                    finishUtterance(utteranceId)
                }
                true
            }
            activeMediaPlayer = player
            player.setOnPreparedListener { preparedPlayer ->
                if (activeMediaPlayer === preparedPlayer && utteranceId == activeUtteranceId) {
                    mutableIsSpeaking.value = true
                    preparedPlayer.start()
                }
            }
            player.prepareAsync()
            true
        }.getOrElse {
            player.release()
            activeMediaPlayer = null
            activeUtteranceId = null
            activeRequest = null
            startRequest(request.copy(clipPath = null))
        }
    }

    private fun stopCurrentPlayback() {
        activeMediaPlayer?.let { player ->
            player.setOnCompletionListener(null)
            player.setOnErrorListener(null)
            player.setOnPreparedListener(null)
            runCatching { if (player.isPlaying) player.stop() }
            player.release()
        }
        activeMediaPlayer = null
    }
}
