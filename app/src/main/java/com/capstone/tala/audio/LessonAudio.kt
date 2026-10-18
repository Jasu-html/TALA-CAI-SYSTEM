package com.capstone.tala.audio

import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.StateFlow

interface LessonAudio {
    val subtitle: StateFlow<String>
    val subtitlesEnabled: StateFlow<Boolean>
    val isSpeaking: StateFlow<Boolean>
    val availability: StateFlow<NarrationAvailability>

    fun speak(
        text: String,
        language: NarrationLanguage = NarrationLanguage.ENGLISH,
        character: NarrationCharacter = NarrationCharacter.TALA
    ): Boolean

    fun enqueue(
        text: String,
        language: NarrationLanguage = NarrationLanguage.ENGLISH,
        character: NarrationCharacter = NarrationCharacter.TALA
    ): Boolean

    fun speakKey(
        key: String,
        args: Map<String, String> = emptyMap(),
        language: NarrationLanguage = NarrationLanguage.ENGLISH,
        character: NarrationCharacter = NarrationCharacter.TALA,
        soundOnly: Boolean = false
    ): Boolean

    fun enqueueKey(
        key: String,
        args: Map<String, String> = emptyMap(),
        language: NarrationLanguage = NarrationLanguage.ENGLISH,
        character: NarrationCharacter = NarrationCharacter.TALA,
        soundOnly: Boolean = false
    ): Boolean

    fun repeat(): Boolean
    fun repeatEvery(seconds: Long)
    fun cancelRepeat()
    fun play(effect: TalaSoundEffect): Boolean
    fun stop()
}

val LocalLessonAudio = staticCompositionLocalOf<LessonAudio> {
    error("LessonAudio must be provided by MainActivity")
}