package com.capstone.tala.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.appSettingsStore by preferencesDataStore(name = "tala_app_settings")

class AppSettingsRepository(
    private val dataStore: DataStore<Preferences>
) {
    val narrationLanguage: Flow<String> = dataStore.data.map {
        it[NARRATION_LANGUAGE_KEY] ?: "en"
    }

    val subtitlesEnabled: Flow<Boolean> = dataStore.data.map {
        it[SUBTITLES_ENABLED_KEY] ?: true
    }

    val ttsRecoveryNoticeShown: Flow<Boolean> = dataStore.data.map {
        it[TTS_RECOVERY_NOTICE_SHOWN_KEY] ?: false
    }

    suspend fun setNarrationLanguage(language: String) {
        dataStore.edit { preferences ->
            preferences[NARRATION_LANGUAGE_KEY] = language
        }
    }

    suspend fun setSubtitlesEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[SUBTITLES_ENABLED_KEY] = enabled
        }
    }

    suspend fun setTtsRecoveryNoticeShown(shown: Boolean) {
        dataStore.edit { preferences ->
            preferences[TTS_RECOVERY_NOTICE_SHOWN_KEY] = shown
        }
    }

    companion object {
        private val NARRATION_LANGUAGE_KEY = stringPreferencesKey("narration_language")
        private val SUBTITLES_ENABLED_KEY = booleanPreferencesKey("subtitles_enabled")
        private val TTS_RECOVERY_NOTICE_SHOWN_KEY = booleanPreferencesKey("tts_recovery_notice_shown")
    }
}

val Context.appSettingsRepository: AppSettingsRepository
    get() = AppSettingsRepository(this.appSettingsStore)
