package com.capstone.tala.audio

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import com.capstone.tala.data.settings.AppSettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LessonAudioViewModel @Inject constructor(
    @ApplicationContext context: Context,
    settings: AppSettingsRepository
) : ViewModel(), LessonAudio {
    private val engine = TalaAudioEngine(context)
    private val mutableSubtitlesEnabled = MutableStateFlow(true)

    override val subtitle: StateFlow<String> = engine.narration.subtitle
    override val subtitlesEnabled: StateFlow<Boolean> = mutableSubtitlesEnabled.asStateFlow()
    override val isSpeaking: StateFlow<Boolean> = engine.narration.isSpeaking
    override val availability: StateFlow<NarrationAvailability> = engine.narration.availability

    init {
        viewModelScope.launch {
            settings.narrationLanguage.collect { tag ->
                engine.setLanguage(NarrationLanguage.fromTag(tag))
            }
        }
        viewModelScope.launch {
            settings.subtitlesEnabled.collect { enabled ->
                mutableSubtitlesEnabled.value = enabled
            }
        }
        viewModelScope.launch {
            engine.narration.availability.collect { state ->
                if (state == NarrationAvailability.ENGLISH_FALLBACK ||
                    state == NarrationAvailability.UNAVAILABLE
                ) {
                    settings.setSubtitlesEnabled(true)
                }
            }
        }
    }

    override fun speak(
        text: String,
        language: NarrationLanguage,
        character: NarrationCharacter
    ): Boolean = engine.speak(text, language, character)

    override fun enqueue(
        text: String,
        language: NarrationLanguage,
        character: NarrationCharacter
    ): Boolean = engine.enqueue(text, language, character)

    override fun speakKey(
        key: String,
        args: Map<String, String>,
        language: NarrationLanguage,
        character: NarrationCharacter,
        soundOnly: Boolean
    ): Boolean = engine.speakKey(key, args, language, character, soundOnly)

    override fun enqueueKey(
        key: String,
        args: Map<String, String>,
        language: NarrationLanguage,
        character: NarrationCharacter,
        soundOnly: Boolean
    ): Boolean = engine.enqueueKey(key, args, language, character, soundOnly)

    override fun repeat(): Boolean = engine.repeat()
    override fun repeatEvery(seconds: Long) = engine.repeatEvery(seconds)
    override fun cancelRepeat() = engine.cancelRepeat()
    override fun play(effect: TalaSoundEffect): Boolean = engine.play(effect)
    override fun stop() = engine.stop()

    fun pauseForBackground() = engine.pauseForBackground()
    fun resumeFromBackground() = engine.resumeFromBackground()

    override fun onCleared() {
        engine.release()
    }
}