package com.capstone.tala.data.db

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.capstone.tala.data.repository.ProgressScoring
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TalaDatabaseDaoTest {
    @Test
    fun `student and level progress can be inserted and read back`() = runBlocking {
        val db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TalaDatabase::class.java
        ).allowMainThreadQueries().build()

        try {
            val student = StudentEntity(id = "student-1", name = "Ayla")
            db.studentDao().upsertStudent(student)

            val progress = LevelProgressEntity(
                studentId = student.id,
                moduleId = "PHONICS",
                levelIndex = 1,
                status = "CURRENT",
                bestStars = 3,
                bestFirstTryAccuracy = 65,
                lastPlayedAt = 123L
            )
            db.levelProgressDao().upsertLevelProgress(progress)

            val savedStudent = db.studentDao().getStudentById(student.id).first()
            val savedProgress = db.levelProgressDao().getModuleProgress(student.id, "PHONICS").first()

            assertNotNull(savedStudent)
            assertEquals(1, savedProgress.size)
            assertEquals("CURRENT", savedProgress[0].status)
            assertEquals(3, ProgressScoring.starsForAccuracy(65))
        } finally {
            db.close()
        }
    }
}
