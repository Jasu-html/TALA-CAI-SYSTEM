package com.capstone.tala.data.repository

import com.capstone.tala.data.db.ActivityResultDao
import com.capstone.tala.data.db.ActivityResultEntity
import com.capstone.tala.data.db.BadgeEarnedDao
import com.capstone.tala.data.db.BadgeEarnedEntity
import com.capstone.tala.data.db.ItemMasteryDao
import com.capstone.tala.data.db.ItemMasteryEntity
import com.capstone.tala.data.db.LevelProgressDao
import com.capstone.tala.data.db.LevelProgressEntity
import com.capstone.tala.data.db.StarTransactionDao
import com.capstone.tala.data.db.StarTransactionEntity
import com.capstone.tala.data.db.StarWalletDao
import com.capstone.tala.data.db.StarWalletEntity
import androidx.room.withTransaction
import com.capstone.tala.data.db.TalaDatabase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

data class ActivityRecord(
    val studentId: String,
    val moduleId: String,
    val levelIndex: Int,
    val activityType: String,
    val itemKey: String,
    val moduleLevelCount: Int,
    val firstTryCorrect: Boolean,
    val attempts: Int,
    val hintsUsed: Int,
    val durationMs: Long,
    val difficulty: String,
    val accuracyPercent: Int,
    val timestamp: Long = System.currentTimeMillis()
)

data class ProgressUpdateResult(
    val starsAwarded: Int,
    val walletBalance: Int,
    val lifetimeStars: Int,
    val levelProgress: LevelProgressEntity
)

class ProgressRecorder @Inject constructor(
    private val database: TalaDatabase,
    private val activityResultDao: ActivityResultDao,
    private val levelProgressDao: LevelProgressDao,
    private val itemMasteryDao: ItemMasteryDao,
    private val starWalletDao: StarWalletDao,
    private val starTransactionDao: StarTransactionDao,
    private val badgeEarnedDao: BadgeEarnedDao
) {
    suspend fun recordActivity(record: ActivityRecord): ProgressUpdateResult = database.withTransaction {
        val resultId = "${record.studentId}-${record.moduleId}-${record.levelIndex}-${record.itemKey}-${record.timestamp}"
        val activityEntity = ActivityResultEntity(
            id = resultId,
            studentId = record.studentId,
            moduleId = record.moduleId,
            levelIndex = record.levelIndex,
            activityType = record.activityType,
            itemKey = record.itemKey,
            firstTryCorrect = record.firstTryCorrect,
            attempts = record.attempts,
            hintsUsed = record.hintsUsed,
            durationMs = record.durationMs,
            difficulty = record.difficulty,
            timestamp = record.timestamp
        )
        activityResultDao.upsertActivityResult(activityEntity)

        val existingMastery = itemMasteryDao.getMasteryForItem(record.studentId, record.itemKey)
        val updatedMastery = if (existingMastery == null) {
            ItemMasteryEntity(
                studentId = record.studentId,
                itemKey = record.itemKey,
                level = record.levelIndex,
                attemptCount = 1,
                firstTryCorrectCount = if (record.firstTryCorrect) 1 else 0,
                lastUpdatedAt = record.timestamp
            )
        } else {
            existingMastery.copy(
                level = record.levelIndex,
                attemptCount = existingMastery.attemptCount + 1,
                firstTryCorrectCount = existingMastery.firstTryCorrectCount + if (record.firstTryCorrect) 1 else 0,
                lastUpdatedAt = record.timestamp
            )
        }
        itemMasteryDao.upsertMastery(updatedMastery)

        val existingProgress = levelProgressDao.getLevelProgress(record.studentId, record.moduleId, record.levelIndex)
        val previousBest = existingProgress?.bestStars ?: 0
        val newBest = maxOf(previousBest, ProgressScoring.starsForAccuracy(record.accuracyPercent))
        val earnedStars = ProgressScoring.extraStarsForReplay(previousBest, newBest)
        val levelStatus = if (newBest > 0) "COMPLETED" else "CURRENT"
        val progressEntity = (existingProgress ?: LevelProgressEntity(
            studentId = record.studentId,
            moduleId = record.moduleId,
            levelIndex = record.levelIndex,
            status = "CURRENT",
            bestStars = 0,
            bestFirstTryAccuracy = 0,
            lastPlayedAt = record.timestamp
        )).copy(
            status = levelStatus,
            bestStars = newBest,
            bestFirstTryAccuracy = maxOf(existingProgress?.bestFirstTryAccuracy ?: 0, record.accuracyPercent),
            lastPlayedAt = record.timestamp
        )
        levelProgressDao.upsertLevelProgress(progressEntity)

        val nextLevel = ProgressScoring.nextLevelIndex(record.levelIndex, record.moduleLevelCount)
        if (nextLevel != null) {
            val nextProgress = levelProgressDao.getLevelProgress(record.studentId, record.moduleId, nextLevel)
            if (nextProgress == null) {
                levelProgressDao.upsertLevelProgress(
                    LevelProgressEntity(
                        studentId = record.studentId,
                        moduleId = record.moduleId,
                        levelIndex = nextLevel,
                        status = "CURRENT",
                        bestStars = 0,
                        bestFirstTryAccuracy = 0,
                        lastPlayedAt = record.timestamp
                    )
                )
            }
        }

        val wallet = starWalletDao.getWalletForStudent(record.studentId) ?: StarWalletEntity(
            studentId = record.studentId,
            balance = 0,
            lifetimeStars = 0
        )
        val updatedWallet = wallet.copy(
            balance = wallet.balance + earnedStars,
            lifetimeStars = wallet.lifetimeStars + earnedStars
        )
        starWalletDao.upsertWallet(updatedWallet)

        if (earnedStars > 0) {
            val transaction = StarTransactionEntity(
                id = "${record.studentId}-${record.moduleId}-${record.levelIndex}-${record.timestamp}",
                studentId = record.studentId,
                delta = earnedStars,
                reason = "level_completion",
                refId = "${record.moduleId}:${record.levelIndex}",
                timestamp = record.timestamp
            )
            starTransactionDao.upsertTransaction(transaction)
        }

        if (updatedWallet.lifetimeStars >= 100) {
            val badge = BadgeEarnedEntity(
                studentId = record.studentId,
                badgeId = "star-collector",
                earnedAt = record.timestamp
            )
            badgeEarnedDao.upsertBadge(badge)
        }

        ProgressUpdateResult(
            starsAwarded = earnedStars,
            walletBalance = updatedWallet.balance,
            lifetimeStars = updatedWallet.lifetimeStars,
            levelProgress = progressEntity
        )
    }
}
