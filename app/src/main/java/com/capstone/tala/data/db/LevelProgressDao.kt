package com.capstone.tala.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LevelProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLevelProgress(progress: LevelProgressEntity)

    @Query("SELECT * FROM level_progress WHERE studentId = :studentId ORDER BY levelIndex ASC")
    fun getProgressForStudent(studentId: String): Flow<List<LevelProgressEntity>>

    @Query("SELECT * FROM level_progress WHERE studentId = :studentId AND moduleId = :moduleId ORDER BY levelIndex ASC")
    fun getModuleProgress(studentId: String, moduleId: String): Flow<List<LevelProgressEntity>>

    @Query("SELECT * FROM level_progress WHERE studentId = :studentId AND moduleId = :moduleId AND levelIndex = :levelIndex LIMIT 1")
    suspend fun getLevelProgress(studentId: String, moduleId: String, levelIndex: Int): LevelProgressEntity?
}
