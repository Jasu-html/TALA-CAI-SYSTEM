package com.capstone.tala.data.repository

import com.capstone.tala.data.db.LevelProgressEntity

object ProgressScoring {
    fun starsForAccuracy(accuracyPercent: Int): Int = when {
        accuracyPercent >= 90 -> 5
        accuracyPercent >= 75 -> 4
        accuracyPercent >= 60 -> 3
        accuracyPercent >= 40 -> 2
        accuracyPercent > 0 -> 1
        else -> 0
    }

    fun isMastered(recentAttempts: List<ItemAttemptResult>): Boolean {
        val lastThree = recentAttempts.takeLast(3)
        if (lastThree.isEmpty()) return false
        val firstTryCount = lastThree.count { it.firstTryCorrect }
        return firstTryCount >= 2
    }

    fun extraStarsForReplay(previousBestStars: Int, newStars: Int): Int {
        return (newStars - previousBestStars).coerceAtLeast(0)
    }

    fun nextLevelIndex(completedLevelIndex: Int, moduleLevelCount: Int): Int? {
        return (completedLevelIndex + 1).takeIf { it <= moduleLevelCount }
    }
}

data class ItemAttemptResult(
    val itemKey: String,
    val firstTryCorrect: Boolean
)
