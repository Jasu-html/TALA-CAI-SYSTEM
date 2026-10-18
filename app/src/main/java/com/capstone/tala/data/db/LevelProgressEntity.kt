package com.capstone.tala.data.db

import androidx.room.Entity

@Entity(
    tableName = "level_progress",
    primaryKeys = ["studentId", "moduleId", "levelIndex"]
)
data class LevelProgressEntity(
    val studentId: String,
    val moduleId: String,
    val levelIndex: Int,
    val status: String = "CURRENT",
    val bestStars: Int = 0,
    val bestFirstTryAccuracy: Int = 0,
    val lastPlayedAt: Long = 0L
)
