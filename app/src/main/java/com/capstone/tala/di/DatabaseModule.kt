package com.capstone.tala.di

import android.content.Context
import androidx.room.Room
import com.capstone.tala.data.db.ActivityResultDao
import com.capstone.tala.data.db.AvatarItemOwnedDao
import com.capstone.tala.data.db.BadgeEarnedDao
import com.capstone.tala.data.db.ItemMasteryDao
import com.capstone.tala.data.db.LevelProgressDao
import com.capstone.tala.data.db.PlaySessionDao
import com.capstone.tala.data.db.QuestAttemptDao
import com.capstone.tala.data.db.StarTransactionDao
import com.capstone.tala.data.db.StarWalletDao
import com.capstone.tala.data.db.StudentDao
import com.capstone.tala.data.db.StudentSettingsDao
import com.capstone.tala.data.db.TalaDatabase
import com.capstone.tala.data.repository.ProgressRecorder
import com.capstone.tala.data.repository.StudentProgressRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideTalaDatabase(@ApplicationContext context: Context): TalaDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            TalaDatabase::class.java,
            "tala_db"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideStudentDao(database: TalaDatabase): StudentDao = database.studentDao()

    @Provides
    fun provideLevelProgressDao(database: TalaDatabase): LevelProgressDao = database.levelProgressDao()

    @Provides
    fun provideActivityResultDao(database: TalaDatabase): ActivityResultDao = database.activityResultDao()

    @Provides
    fun provideQuestAttemptDao(database: TalaDatabase): QuestAttemptDao = database.questAttemptDao()

    @Provides
    fun provideStarWalletDao(database: TalaDatabase): StarWalletDao = database.starWalletDao()

    @Provides
    fun provideStarTransactionDao(database: TalaDatabase): StarTransactionDao = database.starTransactionDao()

    @Provides
    fun provideAvatarItemOwnedDao(database: TalaDatabase): AvatarItemOwnedDao = database.avatarItemOwnedDao()

    @Provides
    fun provideBadgeEarnedDao(database: TalaDatabase): BadgeEarnedDao = database.badgeEarnedDao()

    @Provides
    fun provideItemMasteryDao(database: TalaDatabase): ItemMasteryDao = database.itemMasteryDao()

    @Provides
    fun provideStudentSettingsDao(database: TalaDatabase): StudentSettingsDao = database.studentSettingsDao()

    @Provides
    fun providePlaySessionDao(database: TalaDatabase): PlaySessionDao = database.playSessionDao()

    @Provides
    @Singleton
    fun provideStudentProgressRepository(
        studentDao: StudentDao,
        levelProgressDao: LevelProgressDao,
        activityResultDao: ActivityResultDao,
        questAttemptDao: QuestAttemptDao,
        starWalletDao: StarWalletDao,
        starTransactionDao: StarTransactionDao,
        avatarItemOwnedDao: AvatarItemOwnedDao,
        badgeEarnedDao: BadgeEarnedDao,
        itemMasteryDao: ItemMasteryDao,
        studentSettingsDao: StudentSettingsDao,
        playSessionDao: PlaySessionDao
    ): StudentProgressRepository = StudentProgressRepository(
        studentDao,
        levelProgressDao,
        activityResultDao,
        questAttemptDao,
        starWalletDao,
        starTransactionDao,
        avatarItemOwnedDao,
        badgeEarnedDao,
        itemMasteryDao,
        studentSettingsDao,
        playSessionDao
    )

    @Provides
    @Singleton
    fun provideProgressRecorder(
        database: TalaDatabase,
        activityResultDao: ActivityResultDao,
        levelProgressDao: LevelProgressDao,
        itemMasteryDao: ItemMasteryDao,
        starWalletDao: StarWalletDao,
        starTransactionDao: StarTransactionDao,
        badgeEarnedDao: BadgeEarnedDao
    ): ProgressRecorder = ProgressRecorder(
        database,
        activityResultDao,
        levelProgressDao,
        itemMasteryDao,
        starWalletDao,
        starTransactionDao,
        badgeEarnedDao
    )
}
