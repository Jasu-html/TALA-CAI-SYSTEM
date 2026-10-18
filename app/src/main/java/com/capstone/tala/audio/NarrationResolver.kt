package com.capstone.tala.audio

data class NarrationEntry(
    val text: String,
    val soundText: String? = null
)

sealed interface NarrationResolution {
    data class RecordedClip(
        val assetPath: String,
        val subtitle: String,
        val fallbackText: String
    ) : NarrationResolution

    data class TextToSpeech(
        val text: String,
        val subtitle: String
    ) : NarrationResolution
}

object NarrationResolver {
    fun resolve(
        key: String,
        args: Map<String, String>,
        language: NarrationLanguage,
        soundOnly: Boolean = false,
        hasRecordedClip: (String) -> Boolean,
        findEntry: (NarrationLanguage, String) -> NarrationEntry?
    ): NarrationResolution? {
        val clipPath = "audio/${language.assetTag}/$key.ogg"
        if (hasRecordedClip(clipPath)) {
            val entry = findEntry(language, key)
            val subtitle = entry?.text.orEmpty().interpolate(args)
            val fallbackText = entry?.let {
                if (soundOnly) it.soundText?.takeIf(String::isNotBlank) ?: it.text else it.text
            }.orEmpty().interpolate(args)
            return NarrationResolution.RecordedClip(clipPath, subtitle, fallbackText)
        }

        val entry = findEntry(language, key) ?: return null
        val spokenText = if (soundOnly) entry.soundText?.takeIf(String::isNotBlank) ?: entry.text else entry.text
        return NarrationResolution.TextToSpeech(
            text = spokenText.interpolate(args),
            subtitle = entry.text.interpolate(args)
        )
    }

    private fun String.interpolate(args: Map<String, String>): String =
        args.entries.fold(this) { text, (name, value) -> text.replace("{$name}", value) }
}