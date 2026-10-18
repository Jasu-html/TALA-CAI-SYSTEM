package com.capstone.tala.data.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Entity(
    tableName = "activity_results",
    indices = [Index(value = ["studentId", "moduleId", "levelIndex", "itemKey"]) ]
)
data class ActivityResultEntity(
    @PrimaryKey val id: String,
    val studentId: String,
    val moduleId: String,
    val levelIndex: Int,
    val activityType: String,
    val itemKey: String,
    val firstTryCorrect: Boolean,
    val attempts: Int,
    val hintsUsed: Int,
    val durationMs: Long,
    val difficulty: String,
    val timestamp: Long
)

@Entity(tableName = "quest_attempts")
data class QuestAttemptEntity(
    @PrimaryKey val id: String,
    val studentId: String,
    val round: Int,
    val startedAt: Long,
    val finishedAt: Long,
    val totalItems: Int,
    val firstTryCorrect: Int,
    val starsAwarded: Int
)

@Entity(tableName = "star_wallet")
data class StarWalletEntity(
    @PrimaryKey val studentId: String,
    val balance: Int = 0,
    val lifetimeStars: Int = 0
)

@Entity(tableName = "star_transactions")
data class StarTransactionEntity(
    @PrimaryKey val id: String,
    val studentId: String,
    val delta: Int,
    val reason: String,
    val refId: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "avatar_item_owned",
    primaryKeys = ["studentId", "itemId"]
)
data class AvatarItemOwnedEntity(
    val studentId: String,
    val itemId: String
)

@Entity(
    tableName = "badge_earned",
    primaryKeys = ["studentId", "badgeId"]
)
data class BadgeEarnedEntity(
    val studentId: String,
    val badgeId: String,
    val earnedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "item_mastery",
    primaryKeys = ["studentId", "itemKey"]
)
data class ItemMasteryEntity(
    val studentId: String,
    val itemKey: String,
    val level: Int = 0,
    val attemptCount: Int = 0,
    val firstTryCorrectCount: Int = 0,
    val lastUpdatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "student_settings",
    primaryKeys = ["studentId", "moduleId"]
)
data class StudentSettingsEntity(
    val studentId: String,
    val moduleId: String,
    val difficulty: String = "easy",
    val unlockAllLevels: Boolean = false
)

@Entity(tableName = "play_sessions")
data class PlaySessionEntity(
    @PrimaryKey val id: String,
    val studentId: String,
    val startedAt: Long,
    val endedAt: Long?
)

@Dao
interface ActivityResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertActivityResult(result: ActivityResultEntity)

    @Query("SELECT * FROM activity_results WHERE studentId = :studentId ORDER BY timestamp DESC")
    fun getResultsByStudent(studentId: String): Flow<List<ActivityResultEntity>>

    @Query("SELECT * FROM activity_results WHERE studentId = :studentId AND moduleId = :moduleId AND levelIndex = :levelIndex ORDER BY timestamp DESC")
    fun getResultsForLevel(studentId: String, moduleId: String, levelIndex: Int): Flow<List<ActivityResultEntity>>
}

@Dao
interface QuestAttemptDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAttempt(attempt: QuestAttemptEntity)

    @Query("SELECT * FROM quest_attempts WHERE studentId = :studentId ORDER BY round ASC")
    fun getAttemptsForStudent(studentId: String): Flow<List<QuestAttemptEntity>>
}

@Dao
interface StarWalletDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWallet(wallet: StarWalletEntity)

    @Query("SELECT * FROM star_wallet WHERE studentId = :studentId LIMIT 1")
    suspend fun getWalletForStudent(studentId: String): StarWalletEntity?

    @Query("SELECT * FROM star_wallet WHERE studentId = :studentId LIMIT 1")
    fun observeWalletForStudent(studentId: String): Flow<StarWalletEntity?>
}

@Dao
interface StarTransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTransaction(transaction: StarTransactionEntity)

    @Query("SELECT * FROM star_transactions WHERE studentId = :studentId ORDER BY timestamp DESC")
    fun getTransactionsForStudent(studentId: String): Flow<List<StarTransactionEntity>>
}

@Dao
interface AvatarItemOwnedDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertItem(item: AvatarItemOwnedEntity)

    @Query("SELECT * FROM avatar_item_owned WHERE studentId = :studentId ORDER BY itemId ASC")
    fun getItemsForStudent(studentId: String): Flow<List<AvatarItemOwnedEntity>>
}

@Dao
interface BadgeEarnedDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBadge(badge: BadgeEarnedEntity)

    @Query("SELECT * FROM badge_earned WHERE studentId = :studentId ORDER BY earnedAt DESC")
    fun getBadgesForStudent(studentId: String): Flow<List<BadgeEarnedEntity>>
}

@Dao
interface ItemMasteryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMastery(itemMastery: ItemMasteryEntity)

    @Query("SELECT * FROM item_mastery WHERE studentId = :studentId ORDER BY itemKey ASC")
    fun getMasteryForStudent(studentId: String): Flow<List<ItemMasteryEntity>>

    @Query("SELECT * FROM item_mastery WHERE studentId = :studentId AND itemKey = :itemKey LIMIT 1")
    suspend fun getMasteryForItem(studentId: String, itemKey: String): ItemMasteryEntity?
}

@Dao
interface StudentSettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSettings(settings: StudentSettingsEntity)

    @Query("SELECT * FROM student_settings WHERE studentId = :studentId ORDER BY moduleId ASC")
    fun getSettingsForStudent(studentId: String): Flow<List<StudentSettingsEntity>>
}

@Dao
interface PlaySessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSession(session: PlaySessionEntity)

    @Query("SELECT * FROM play_sessions WHERE studentId = :studentId ORDER BY startedAt DESC")
    fun getSessionsForStudent(studentId: String): Flow<List<PlaySessionEntity>>
}
