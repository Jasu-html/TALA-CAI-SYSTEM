package com.capstone.tala.domain.content

import kotlinx.serialization.Serializable

@Serializable
data class ModuleContent(
    val id: String,
    val title: String,
    val description: String = "",
    val levels: List<LevelContent> = emptyList()
)

@Serializable
data class LevelContent(
    val index: Int,
    val title: String,
    val lessonText: String,
    val prompt: String,
    val choices: List<String> = emptyList(),
    val correctAnswer: String = "",
    val guide: String = "Tala"
)
