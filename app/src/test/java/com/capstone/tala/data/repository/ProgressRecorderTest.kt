package com.capstone.tala.data.repository

import com.capstone.tala.data.db.StarWalletEntity
import com.capstone.tala.data.db.StudentEntity
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.capstone.tala.data.db.TalaDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ProgressRecorderTest {
    @Test
    fun `accuracy maps to star values from 1 to 5`() {
        assertEquals(0, ProgressScoring.starsForAccuracy(0))
        assertEquals(1, ProgressScoring.starsForAccuracy(1))
        assertEquals(1, ProgressScoring.starsForAccuracy(39))
        assertEquals(2, ProgressScoring.starsForAccuracy(40))
        assertEquals(2, ProgressScoring.starsForAccuracy(59))
        assertEquals(3, ProgressScoring.starsForAccuracy(60))
        assertEquals(3, ProgressScoring.starsForAccuracy(74))
        assertEquals(4, ProgressScoring.starsForAccuracy(75))
        assertEquals(4, ProgressScoring.starsForAccuracy(89))
        assertEquals(5, ProgressScoring.starsForAccuracy(90))
    }

    @Test
    fun `mastery rule is satisfied after two first-try correct in recent three attempts`() {
        val attempts = listOf(
            ItemAttemptResult(firstTryCorrect = true, itemKey = "letter_B"),
            ItemAttemptResult(firstTryCorrect = false, itemKey = "letter_B"),
            ItemAttemptResult(firstTryCorrect = true, itemKey = "letter_B")
        )

        assertTrue(ProgressScoring.isMastered(attempts))
    }

    @Test
    fun `replay adds only the extra stars beyond the previous best`() {
        assertEquals(2, ProgressScoring.extraStarsForReplay(previousBestStars = 3, newStars = 5))
        assertEquals(0, ProgressScoring.extraStarsForReplay(previousBestStars = 5, newStars = 5))
    }

    @Test
    fun `wallet and lifetime stars are tracked separately`() {
        val wallet = StarWalletEntity(studentId = "student-1", balance = 12, lifetimeStars = 55)
        val purchase = StarWalletEntity(studentId = "student-1", balance = 9, lifetimeStars = 55)

        assertEquals(12, wallet.balance)
        assertEquals(55, wallet.lifetimeStars)
        assertEquals(9, purchase.balance)
        assertEquals(55, purchase.lifetimeStars)
    }

    @Test
    fun `level unlock order advances to the next indexed level`() {
        assertEquals(2, ProgressScoring.nextLevelIndex(completedLevelIndex = 1, moduleLevelCount = 20))
    }

    @Test
    fun `recording persists all progress and badge updates in one transaction`() = runBlocking {
        val database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TalaDatabase::class.java
        ).allowMainThreadQueries().build()

        try {
            val studentId = "student-1"
            database.studentDao().upsertStudent(StudentEntity(id = studentId, name = "Mia"))
            database.starWalletDao().upsertWallet(
                StarWalletEntity(studentId = studentId, balance = 0, lifetimeStars = 97)
            )

            val update = newRecorder(database).recordActivity(
                activityRecord(studentId, "letter_B", accuracyPercent = 95, timestamp = 123L)
            )

            val activityResults = database.activityResultDao().getResultsByStudent(studentId).first()
            val levelProgress = database.levelProgressDao()
                .getLevelProgress(studentId, "PHONICS", 1)
            val nextLevelProgress = database.levelProgressDao()
                .getLevelProgress(studentId, "PHONICS", 2)
            val mastery = database.itemMasteryDao().getMasteryForItem(studentId, "letter_B")
            val wallet = database.starWalletDao().getWalletForStudent(studentId)
            val transactions = database.starTransactionDao().getTransactionsForStudent(studentId).first()
            val badges = database.badgeEarnedDao().getBadgesForStudent(studentId).first()

            assertEquals(1, activityResults.size)
            assertEquals("letter_B", activityResults.single().itemKey)
            assertNotNull(levelProgress)
            assertEquals("COMPLETED", levelProgress?.status)
            assertEquals(5, levelProgress?.bestStars)
            assertEquals("CURRENT", nextLevelProgress?.status)
            assertNotNull(mastery)
            assertEquals(1, mastery?.attemptCount)
            assertEquals(1, mastery?.firstTryCorrectCount)
            assertEquals(5, update.starsAwarded)
            assertEquals(5, wallet?.balance)
            assertEquals(102, wallet?.lifetimeStars)
            assertEquals(1, transactions.size)
            assertEquals(5, transactions.single().delta)
            assertEquals(listOf("star-collector"), badges.map { it.badgeId })
        } finally {
            database.close()
        }
    }

    @Test
    fun `badge is awarded only once after multiple results exceed the lifetime threshold`() = runBlocking {
        val database = newDatabase()
        try {
            val studentId = "student-1"
            database.studentDao().upsertStudent(StudentEntity(id = studentId, name = "Mia"))
            database.starWalletDao().upsertWallet(
                StarWalletEntity(studentId = studentId, balance = 0, lifetimeStars = 97)
            )
            val recorder = newRecorder(database)

            recorder.recordActivity(activityRecord(studentId, "item-1", accuracyPercent = 60, timestamp = 1L))
            recorder.recordActivity(activityRecord(studentId, "item-2", accuracyPercent = 100, timestamp = 2L))

            val badges = database.badgeEarnedDao().getBadgesForStudent(studentId).first()
            assertEquals(1, badges.size)
            assertEquals("star-collector", badges.single().badgeId)
        } finally {
            database.close()
        }
    }

    @Test
    fun `equal and lower star replays do not change wallet level status or transactions`() = runBlocking {
        val database = newDatabase()
        try {
            val studentId = "student-1"
            database.studentDao().upsertStudent(StudentEntity(id = studentId, name = "Mia"))
            database.starWalletDao().upsertWallet(
                StarWalletEntity(studentId = studentId, balance = 10, lifetimeStars = 20)
            )
            val recorder = newRecorder(database)

            recorder.recordActivity(activityRecord(studentId, "item-1", accuracyPercent = 90, timestamp = 1L))
            val originalLevel = database.levelProgressDao().getLevelProgress(studentId, "PHONICS", 1)
            recorder.recordActivity(activityRecord(studentId, "item-1", accuracyPercent = 90, timestamp = 2L))
            recorder.recordActivity(activityRecord(studentId, "item-1", accuracyPercent = 75, timestamp = 3L))

            val wallet = database.starWalletDao().getWalletForStudent(studentId)
            val currentLevel = database.levelProgressDao().getLevelProgress(studentId, "PHONICS", 1)
            val transactions = database.starTransactionDao().getTransactionsForStudent(studentId).first()

            assertEquals(15, wallet?.balance)
            assertEquals(25, wallet?.lifetimeStars)
            assertEquals(originalLevel?.status, currentLevel?.status)
            assertEquals("COMPLETED", currentLevel?.status)
            assertEquals(1, transactions.size)
            assertEquals(5, transactions.single().delta)
        } finally {
            database.close()
        }
    }

    @Test
    fun `completing the last module level does not create another level`() = runBlocking {
        val database = newDatabase()
        try {
            val studentId = "student-1"
            database.studentDao().upsertStudent(StudentEntity(id = studentId, name = "Mia"))

            newRecorder(database).recordActivity(
                activityRecord(
                    studentId = studentId,
                    itemKey = "final-item",
                    accuracyPercent = 90,
                    timestamp = 1L,
                    levelIndex = 2,
                    moduleLevelCount = 2
                )
            )

            assertEquals("COMPLETED", database.levelProgressDao()
                .getLevelProgress(studentId, "PHONICS", 2)?.status)
            assertNull(database.levelProgressDao().getLevelProgress(studentId, "PHONICS", 3))
        } finally {
            database.close()
        }
    }

    private fun newDatabase(): TalaDatabase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        TalaDatabase::class.java
    ).allowMainThreadQueries().build()

    private fun newRecorder(database: TalaDatabase) = ProgressRecorder(
        database = database,
        activityResultDao = database.activityResultDao(),
        levelProgressDao = database.levelProgressDao(),
        itemMasteryDao = database.itemMasteryDao(),
        starWalletDao = database.starWalletDao(),
        starTransactionDao = database.starTransactionDao(),
        badgeEarnedDao = database.badgeEarnedDao()
    )

    private fun activityRecord(
        studentId: String,
        itemKey: String,
        accuracyPercent: Int,
        timestamp: Long,
        levelIndex: Int = 1,
        moduleLevelCount: Int = 20
    ) = ActivityRecord(
        studentId = studentId,
        moduleId = "PHONICS",
        levelIndex = levelIndex,
        activityType = "letter",
        itemKey = itemKey,
        moduleLevelCount = moduleLevelCount,
        firstTryCorrect = true,
        attempts = 1,
        hintsUsed = 0,
        durationMs = 1000L,
        difficulty = "easy",
        accuracyPercent = accuracyPercent,
        timestamp = timestamp
    )
}
