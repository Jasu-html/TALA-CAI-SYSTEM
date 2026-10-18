package com.capstone.tala.audio

import android.content.Context
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class NarrationAssetCatalog(context: Context) {
    private val assets = context.applicationContext.assets
    private val cache = mutableMapOf<NarrationLanguage, Map<String, NarrationEntry>>()
    private val json = Json { ignoreUnknownKeys = true }

    fun resolve(
        key: String,
        args: Map<String, String>,
        language: NarrationLanguage,
        soundOnly: Boolean = false
    ): NarrationResolution? = NarrationResolver.resolve(
        key = key,
        args = args,
        language = language,
        soundOnly = soundOnly,
        hasRecordedClip = { assetPath ->
            runCatching { assets.open(assetPath).use { } }.isSuccess
        },
        findEntry = { requestedLanguage, entryKey -> load(requestedLanguage)[entryKey] }
    )

    private fun load(language: NarrationLanguage): Map<String, NarrationEntry> =
        cache.getOrPut(language) {
            runCatching {
                assets.open("content/strings_${language.assetTag}.json")
                    .bufferedReader()
                    .use { it.readText() }
                    .let(json::parseToJsonElement)
                    .jsonObject
                    .mapNotNull { (key, element) ->
                        val value = element as? JsonObject
                        if (value != null) {
                            val text = value["text"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
                            key to NarrationEntry(
                                text = text,
                                soundText = value["soundText"]?.jsonPrimitive?.contentOrNull
                            )
                        } else {
                            key to NarrationEntry(element.jsonPrimitive.content)
                        }
                    }
                    .toMap()
            }.getOrDefault(emptyMap())
        }
}