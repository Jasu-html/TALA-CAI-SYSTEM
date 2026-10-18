package com.capstone.tala.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        StudentEntity::class,
        LevelProgressEntity::class,
        ActivityResultEntity::class,
        QuestAttemptEntity::class,
        StarWalletEntity::class,
        StarTransactionEntity::class,
        AvatarItemOwnedEntity::class,
        BadgeEarnedEntity::class,
        ItemMasteryEntity::class,
        StudentSettingsEntity::class,
        PlaySessionEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class TalaDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao
    abstract fun levelProgressDao(): LevelProgressDao
    abstract fun activityResultDao(): ActivityResultDao
    abstract fun questAttemptDao(): QuestAttemptDao
    abstract fun starWalletDao(): StarWalletDao
    abstract fun starTransactionDao(): StarTransactionDao
    abstract fun avatarItemOwnedDao(): AvatarItemOwnedDao
    abstract fun badgeEarnedDao(): BadgeEarnedDao
    abstract fun itemMasteryDao(): ItemMasteryDao
    abstract fun studentSettingsDao(): StudentSettingsDao
    abstract fun playSessionDao(): PlaySessionDao
}
