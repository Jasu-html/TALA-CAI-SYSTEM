package com.capstone.tala.audio

import java.util.Locale

data class NarrationLanguageResolution(
    val locale: Locale,
    val usingFallback: Boolean,
    val available: Boolean
)

object NarrationLanguageResolver {
    fun resolve(
        requested: NarrationLanguage,
        isAvailable: (Locale) -> Boolean
    ): NarrationLanguageResolution {
        if (isAvailable(requested.locale)) {
            return NarrationLanguageResolution(requested.locale, usingFallback = false, available = true)
        }

        val fallback = Locale.US
        if (requested.locale != fallback && isAvailable(fallback)) {
            return NarrationLanguageResolution(fallback, usingFallback = true, available = true)
        }

        return NarrationLanguageResolution(fallback, usingFallback = true, available = false)
    }
}