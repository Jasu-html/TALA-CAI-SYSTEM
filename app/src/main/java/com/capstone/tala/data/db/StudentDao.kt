package com.capstone.tala.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStudent(student: StudentEntity)

    @Query("SELECT * FROM students ORDER BY createdAt DESC")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE id = :studentId")
    fun getStudentById(studentId: String): Flow<StudentEntity?>
}
