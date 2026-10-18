package com.capstone.tala.audio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Locale

class NarrationLanguageResolverTest {
    @Test
    fun `uses requested language when installed`() {
        val result = NarrationLanguageResolver.resolve(NarrationLanguage.FILIPINO) { true }

        assertEquals(NarrationLanguage.FILIPINO.locale, result.locale)
        assertFalse(result.usingFallback)
        assertTrue(result.available)
    }

    @Test
    fun `falls back to English when requested language is unavailable`() {
        val result = NarrationLanguageResolver.resolve(NarrationLanguage.FILIPINO) { locale ->
            locale == Locale.US
        }

        assertEquals(Locale.US, result.locale)
        assertTrue(result.usingFallback)
        assertTrue(result.available)
    }

    @Test
    fun `Filipino narration keeps Filipino text and subtitle when voice falls back to English`() {
        val voice = NarrationLanguageResolver.resolve(NarrationLanguage.FILIPINO) { locale ->
            locale == Locale.US
        }
        val line = NarrationResolver.resolve(
            key = "debug.greeting",
            args = mapOf("name" to "Tala"),
            language = NarrationLanguage.FILIPINO,
            hasRecordedClip = { false },
            findEntry = { _, _ -> NarrationEntry("Kumusta, {name}! Sama-sama tayong matuto.") }
        ) as NarrationResolution.TextToSpeech

        assertEquals(Locale.US, voice.locale)
        assertEquals("Kumusta, Tala! Sama-sama tayong matuto.", line.text)
        assertEquals(line.text, line.subtitle)
    }

    @Test
    fun `reports unavailable when neither requested nor fallback language is installed`() {
        val result = NarrationLanguageResolver.resolve(NarrationLanguage.FILIPINO) { false }

        assertEquals(Locale.US, result.locale)
        assertTrue(result.usingFallback)
        assertFalse(result.available)
    }

    @Test
    fun `recorded clip is resolved before localized text`() {
        val calls = mutableListOf<String>()
        val result = NarrationResolver.resolve(
            key = "lesson.hello",
            args = emptyMap(),
            language = NarrationLanguage.FILIPINO,
            hasRecordedClip = { path ->
                calls += "clip:$path"
                true
            },
            findEntry = { _, _ ->
                calls += "text"
                NarrationEntry("Kumusta")
            }
        )

        assertEquals(
            NarrationResolution.RecordedClip("audio/fil/lesson.hello.ogg", "Kumusta", "Kumusta"),
            result
        )
        assertEquals(listOf("clip:audio/fil/lesson.hello.ogg", "text"), calls)
    }

    @Test
    fun `missing recorded clip resolves localized text for TTS`() {
        val result = NarrationResolver.resolve(
            key = "debug.greeting",
            args = mapOf("name" to "Ayla"),
            language = NarrationLanguage.FILIPINO,
            hasRecordedClip = { false },
            findEntry = { _, _ -> NarrationEntry("Kumusta, {name}!") }
        )

        assertEquals(NarrationResolution.TextToSpeech("Kumusta, Ayla!", "Kumusta, Ayla!"), result)
    }

    @Test
    fun `letter sound uses soundText while subtitle keeps the full localized prompt`() {
        val result = NarrationResolver.resolve(
            key = "letter.B",
            args = emptyMap(),
            language = NarrationLanguage.ENGLISH,
            soundOnly = true,
            hasRecordedClip = { false },
            findEntry = { _, _ -> NarrationEntry("B says buh, as in ball.", "buh") }
        )

        assertEquals(NarrationResolution.TextToSpeech("buh", "B says buh, as in ball."), result)
    }

    @Test
    fun `missing localized key does not invent a spoken line`() {
        val result = NarrationResolver.resolve(
            key = "missing.key",
            args = emptyMap(),
            language = NarrationLanguage.FILIPINO,
            hasRecordedClip = { false },
            findEntry = { _, _ -> null }
        )

        assertNull(result)
    }
}