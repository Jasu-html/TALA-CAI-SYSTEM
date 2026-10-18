package com.capstone.tala.data.content

import android.content.Context
import com.capstone.tala.domain.content.ModuleContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class TalaContentLoader(
    private val context: Context
) {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    suspend fun loadModule(moduleId: String): ModuleContent = withContext(Dispatchers.IO) {
        val normalizedId = moduleId.trim().lowercase()
        val assetName = "content/$normalizedId.json"
        val input = context.assets.open(assetName)
            .bufferedReader()
            .use { it.readText() }

        json.decodeFromString(ModuleContent.serializer(), input)
    }
}
