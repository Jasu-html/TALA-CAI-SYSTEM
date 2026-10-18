package com.capstone.tala.audio

import java.util.Locale

enum class NarrationLanguage(
    val displayName: String,
    val locale: Locale,
    val assetTag: String
) {
    ENGLISH("English", Locale("en", "PH"), "en"),
    FILIPINO("Filipino", Locale("fil", "PH"), "fil");

    companion object {
        fun fromTag(tag: String?): NarrationLanguage = when (tag?.lowercase()) {
            "fil", "filipino", "tl", "tagalog" -> FILIPINO
            else -> ENGLISH
        }
    }
}

enum class NarrationCharacter(
    val displayName: String,
    val pitch: Float,
    val speechRate: Float
) {
    TALA("Tala", 1.0f, 0.85f),
    LIKHA("Likha", 1.12f, 0.88f),
    BILANG("Bilang", 0.92f, 0.82f),
    TITIK("Titik", 1.18f, 0.9f)
}
