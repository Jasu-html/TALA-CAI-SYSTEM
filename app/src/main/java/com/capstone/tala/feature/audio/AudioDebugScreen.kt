package com.capstone.tala.feature.audio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.capstone.tala.audio.LocalLessonAudio
import com.capstone.tala.audio.NarrationAvailability
import com.capstone.tala.audio.NarrationCharacter
import com.capstone.tala.audio.NarrationLanguage
import com.capstone.tala.audio.SubtitleBar
import com.capstone.tala.audio.TalaSoundEffect

@Composable
fun AudioDebugScreen() {
    val audio = LocalLessonAudio.current
    val subtitle by audio.subtitle.collectAsState()
    val subtitlesEnabled by audio.subtitlesEnabled.collectAsState()
    val availability by audio.availability.collectAsState()
    var repeatEnabled by rememberSaveable { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Audio check", style = MaterialTheme.typography.headlineMedium)
            Text("TTS: ${availability.label()}", style = MaterialTheme.typography.bodyMedium)
            if (subtitlesEnabled && subtitle.isNotBlank()) SubtitleBar(subtitle)
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    audio.speakKey(
                        key = "debug.greeting",
                        args = mapOf("name" to "Tala"),
                        language = NarrationLanguage.ENGLISH
                    )
                }) { Text("English sample") }
                Button(onClick = {
                    audio.speakKey(
                        key = "debug.greeting",
                        args = mapOf("name" to "Tala"),
                        language = NarrationLanguage.FILIPINO
                    )
                }) { Text("Filipino sample") }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { audio.repeat() }) { Text("Repeat") }
                Button(onClick = { audio.stop(); repeatEnabled = false }) { Text("Stop") }
                Button(onClick = {
                    repeatEnabled = !repeatEnabled
                    if (repeatEnabled) audio.repeatEvery(8) else audio.cancelRepeat()
                }) { Text(if (repeatEnabled) "Stop 8-second repeat" else "Repeat every 8 seconds") }
            }
        }
        item {
            Text("Sound effects", style = MaterialTheme.typography.titleMedium)
        }
        item {
            Text("Character voices", style = MaterialTheme.typography.titleMedium)
        }
        items(NarrationCharacter.entries) { character ->
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    audio.speakKey(
                        key = "debug.character",
                        args = mapOf("character" to character.displayName),
                        language = NarrationLanguage.ENGLISH,
                        character = character
                    )
                }
            ) { Text(character.displayName) }
        }
        item {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    audio.speakKey("letter.B", language = NarrationLanguage.ENGLISH, soundOnly = true)
                }
            ) { Text("Letter B sound") }
        }
        items(TalaSoundEffect.entries) { effect ->
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { audio.play(effect) }
            ) {
                Text(effect.label())
            }
        }
    }
}

private fun NarrationAvailability.label(): String = when (this) {
    NarrationAvailability.INITIALIZING -> "Initializing"
    NarrationAvailability.REQUESTED_LANGUAGE -> "Requested language available"
    NarrationAvailability.ENGLISH_FALLBACK -> "Using English fallback"
    NarrationAvailability.UNAVAILABLE -> "Unavailable; subtitles remain available"
}

private fun TalaSoundEffect.label(): String = when (this) {
    TalaSoundEffect.TAP -> "Tap"
    TalaSoundEffect.PICKUP -> "Pick up"
    TalaSoundEffect.DROP -> "Drop"
    TalaSoundEffect.CORRECT -> "Correct chime"
    TalaSoundEffect.TRY_AGAIN -> "Gentle try again"
    TalaSoundEffect.STAR -> "Star earned"
    TalaSoundEffect.CELEBRATE -> "Celebration"
}